package deakin.gopher.guardian.view.general

import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import deakin.gopher.guardian.databinding.DialogPatientReassignmentBinding
import deakin.gopher.guardian.model.ApiErrorResponse
import deakin.gopher.guardian.model.Patient
import deakin.gopher.guardian.model.ReassignPatientRequest
import deakin.gopher.guardian.model.StaffMember
import deakin.gopher.guardian.model.login.SessionManager
import deakin.gopher.guardian.services.api.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PatientReassignmentDialog(
    private val activity: AppCompatActivity,
    private val patient: Patient,
    private val onReassigned: () -> Unit,
) {
    private lateinit var binding: DialogPatientReassignmentBinding
    private lateinit var dialog: AlertDialog

    private var caretakerOptions: List<SpinnerOption> = emptyList()
    private var nurseOptions: List<SpinnerOption> = emptyList()
    private var doctorOptions: List<SpinnerOption> = emptyList()

    fun show() {
        binding =
            DialogPatientReassignmentBinding.inflate(LayoutInflater.from(activity))
        dialog =
            AlertDialog.Builder(activity)
                .setView(binding.root)
                .setCancelable(true)
                .create()

        setupCurrentValues()
        setupButtons()
        dialog.show()
        loadStaffOptions()
    }

    private fun setupCurrentValues() {
        binding.tvCurrentCaretaker.text = patient.caretaker?.name ?: "Not assigned"
        binding.tvCurrentNurse.text = patient.assignedNurses.firstOrNull()?.name ?: "Not assigned"
        binding.tvCurrentDoctor.text = patient.assignedDoctor?.name ?: "Not assigned"
    }

    private fun setupButtons() {
        binding.buttonCancel.setOnClickListener {
            dialog.dismiss()
        }

        binding.buttonSave.setOnClickListener {
            submitReassignment()
        }
    }

    private fun loadStaffOptions() {
        setLoadingState(isLoading = true, loadingMessage = "Loading available staff...")

        val token = "Bearer ${SessionManager.getToken()}"
        val organizationId = SessionManager.getCurrentUser().organization

        activity.lifecycleScope.launch {
            val result =
                withContext(Dispatchers.IO) {
                    val caretakersDeferred =
                        async {
                            ApiClient.apiService.getAdminStaff(
                                token = token,
                                organizationId = organizationId,
                                role = "caretaker",
                            )
                        }
                    val nursesDeferred =
                        async {
                            ApiClient.apiService.getAdminStaff(
                                token = token,
                                organizationId = organizationId,
                                role = "nurse",
                            )
                        }
                    val doctorsDeferred =
                        async {
                            ApiClient.apiService.getAdminStaff(
                                token = token,
                                organizationId = organizationId,
                                role = "doctor",
                            )
                        }

                    Triple(
                        caretakersDeferred.await(),
                        nursesDeferred.await(),
                        doctorsDeferred.await(),
                    )
                }

            val caretakerResponse = result.first
            val nurseResponse = result.second
            val doctorResponse = result.third

            if (
                caretakerResponse.isSuccessful &&
                nurseResponse.isSuccessful &&
                doctorResponse.isSuccessful
            ) {
                caretakerOptions = caretakerResponse.body()?.staff.toSpinnerOptions().orEmpty()
                nurseOptions = nurseResponse.body()?.staff.toSpinnerOptions().orEmpty()
                doctorOptions = doctorResponse.body()?.staff.toSpinnerOptions().orEmpty()

                bindSpinner(
                    options = caretakerOptions,
                    spinnerType = SpinnerType.Caretaker,
                    selectedId = patient.caretaker?.id,
                )
                bindSpinner(
                    options = nurseOptions,
                    spinnerType = SpinnerType.Nurse,
                    selectedId = patient.assignedNurses.firstOrNull()?.id,
                )
                bindSpinner(
                    options = doctorOptions,
                    spinnerType = SpinnerType.Doctor,
                    selectedId = patient.assignedDoctor?.id,
                )

                setLoadingState(isLoading = false)
            } else {
                val errorMessage =
                    readErrorMessage(
                        caretakerResponse.errorBody()?.string()
                            ?: nurseResponse.errorBody()?.string()
                            ?: doctorResponse.errorBody()?.string(),
                    ) ?: "Failed to load staff list"

                setLoadingState(isLoading = false)
                binding.tvError.visibility = View.VISIBLE
                binding.tvError.text = errorMessage
                binding.buttonSave.isEnabled = false
            }
        }
    }

    private fun bindSpinner(
        options: List<SpinnerOption>,
        spinnerType: SpinnerType,
        selectedId: String?,
    ) {
        val placeholder =
            when (spinnerType) {
                SpinnerType.Caretaker -> "Select caretaker"
                SpinnerType.Nurse -> "Select nurse"
                SpinnerType.Doctor -> "Select doctor"
            }

        val items = listOf(placeholder) + options.map { it.label }
        val adapter =
            ArrayAdapter(
                activity,
                android.R.layout.simple_spinner_item,
                items,
            ).also {
                it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }

        val selectedIndex =
            options.indexOfFirst { it.id == selectedId }
                .takeIf { it >= 0 }
                ?.plus(1) ?: 0

        when (spinnerType) {
            SpinnerType.Caretaker -> {
                binding.spinnerCaretaker.adapter = adapter
                binding.spinnerCaretaker.setSelection(selectedIndex)
            }
            SpinnerType.Nurse -> {
                binding.spinnerNurse.adapter = adapter
                binding.spinnerNurse.setSelection(selectedIndex)
            }
            SpinnerType.Doctor -> {
                binding.spinnerDoctor.adapter = adapter
                binding.spinnerDoctor.setSelection(selectedIndex)
            }
        }
    }

    private fun submitReassignment() {
        val caretakerId =
            caretakerOptions.getOrNull(binding.spinnerCaretaker.selectedItemPosition - 1)?.id
        val nurseId =
            nurseOptions.getOrNull(binding.spinnerNurse.selectedItemPosition - 1)?.id
        val doctorId =
            doctorOptions.getOrNull(binding.spinnerDoctor.selectedItemPosition - 1)?.id

        if (caretakerId.isNullOrBlank() || nurseId.isNullOrBlank() || doctorId.isNullOrBlank()) {
            binding.tvError.visibility = View.VISIBLE
            binding.tvError.text = "Please select a caretaker, nurse, and doctor."
            return
        }

        binding.tvError.visibility = View.GONE
        setLoadingState(isLoading = true, loadingMessage = "Saving reassignment...")

        val token = "Bearer ${SessionManager.getToken()}"
        val request =
            ReassignPatientRequest(
                caretakerId = caretakerId,
                nurseId = nurseId,
                doctorId = doctorId,
            )

        activity.lifecycleScope.launch {
            val response =
                withContext(Dispatchers.IO) {
                    try {
                        ApiClient.apiService.reassignPatient(token, patient.id, request)
                    } catch (exception: Exception) {
                        null
                    }
                }

            setLoadingState(isLoading = false)

            if (response?.isSuccessful == true) {
                Toast.makeText(
                    activity,
                    response.body()?.apiMessage ?: "Patient reassigned successfully",
                    Toast.LENGTH_SHORT,
                ).show()
                dialog.dismiss()
                onReassigned()
            } else {
                binding.tvError.visibility = View.VISIBLE
                binding.tvError.text =
                    readErrorMessage(response?.errorBody()?.string())
                        ?: "Failed to reassign patient"
            }
        }
    }

    private fun setLoadingState(
        isLoading: Boolean,
        loadingMessage: String = "",
    ) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.tvLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.tvLoading.text = loadingMessage
        binding.spinnerCaretaker.isEnabled = !isLoading
        binding.spinnerNurse.isEnabled = !isLoading
        binding.spinnerDoctor.isEnabled = !isLoading
        binding.buttonCancel.isEnabled = !isLoading
        binding.buttonSave.isEnabled = !isLoading
    }

    private fun readErrorMessage(errorBody: String?): String? {
        if (errorBody.isNullOrBlank()) {
            return null
        }
        return try {
            Gson().fromJson(errorBody, ApiErrorResponse::class.java)?.apiError
        } catch (exception: Exception) {
            null
        }
    }

    private fun List<StaffMember>?.toSpinnerOptions(): List<SpinnerOption> {
        return this.orEmpty().map { staff ->
            SpinnerOption(
                id = staff.id,
                label = staff.displayLabel(),
            )
        }
    }

    private data class SpinnerOption(
        val id: String,
        val label: String,
    )

    private enum class SpinnerType {
        Caretaker,
        Nurse,
        Doctor,
    }
}
