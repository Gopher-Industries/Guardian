package deakin.gopher.guardian.view.general

import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import deakin.gopher.guardian.databinding.DialogPatientReassignmentBinding
import deakin.gopher.guardian.model.ApiErrorResponse
import deakin.gopher.guardian.model.Patient
import deakin.gopher.guardian.model.ReassignPatientRequest
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
        runCatching {
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
        }.onFailure { exception ->
            Toast.makeText(
                activity,
                exception.message ?: "Unable to open reassignment form",
                Toast.LENGTH_SHORT,
            ).show()
        }
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

        val token = try {
            "Bearer ${SessionManager.getToken()}"
        } catch (exception: Exception) {
            showLoadError("Session expired. Please log in again.")
            return
        }

        activity.lifecycleScope.launch {
            val result =
                withContext(Dispatchers.IO) {
                    runCatching {
                        val caretakersDeferred =
                            async { ApiClient.apiService.getCaretakers(token) }
                        val nursesDeferred =
                            async { ApiClient.apiService.getNurses(token) }
                        val doctorsDeferred =
                            async { ApiClient.apiService.getDoctors(token) }

                        Triple(
                            caretakersDeferred.await(),
                            nursesDeferred.await(),
                            doctorsDeferred.await(),
                        )
                    }
                }

            result.onSuccess { responses ->
                val caretakerResponse = responses.first
                val nurseResponse = responses.second
                val doctorResponse = responses.third

                if (
                    caretakerResponse.isSuccessful &&
                    nurseResponse.isSuccessful &&
                    doctorResponse.isSuccessful
                ) {
                    caretakerOptions = parseStaffOptions(caretakerResponse.body(), "caretakers")
                    nurseOptions = parseStaffOptions(nurseResponse.body(), "nurses")
                    doctorOptions = parseStaffOptions(doctorResponse.body(), "doctors")

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

                    if (
                        caretakerOptions.isEmpty() ||
                        nurseOptions.isEmpty() ||
                        doctorOptions.isEmpty()
                    ) {
                        binding.tvError.visibility = View.VISIBLE
                        binding.tvError.text =
                            "Some staff lists are empty. Reassignment may be unavailable."
                    }
                } else {
                    val errorMessage =
                        readErrorMessage(
                            caretakerResponse.errorBody()?.string()
                                ?: nurseResponse.errorBody()?.string()
                                ?: doctorResponse.errorBody()?.string(),
                        ) ?: "Failed to load staff list"

                    showLoadError(errorMessage)
                }
            }.onFailure { exception ->
                showLoadError(exception.message ?: "Failed to load staff list")
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

        val token = try {
            "Bearer ${SessionManager.getToken()}"
        } catch (exception: Exception) {
            binding.tvError.visibility = View.VISIBLE
            binding.tvError.text = "Session expired. Please log in again."
            return
        }

        binding.tvError.visibility = View.GONE
        setLoadingState(isLoading = true, loadingMessage = "Saving reassignment...")

        val request =
            ReassignPatientRequest(
                caretakerId = caretakerId,
                nurseId = nurseId,
                doctorId = doctorId,
            )

        activity.lifecycleScope.launch {
            val response =
                withContext(Dispatchers.IO) {
                    runCatching {
                        ApiClient.apiService.reassignPatient(token, patient.id, request)
                    }.getOrNull()
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

    private fun showLoadError(message: String) {
        setLoadingState(isLoading = false)
        binding.tvError.visibility = View.VISIBLE
        binding.tvError.text = message
        binding.buttonSave.isEnabled = false
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
            val errorResponse = Gson().fromJson(errorBody, ApiErrorResponse::class.java)
            errorResponse?.apiError ?: Gson().fromJson(errorBody, JsonObject::class.java)
                ?.getStringOrNull("message")
        } catch (exception: Exception) {
            null
        }
    }

    private fun parseStaffOptions(
        body: JsonElement?,
        preferredWrapperKey: String,
    ): List<SpinnerOption> {
        val staffArray = body.findStaffArray(preferredWrapperKey) ?: return emptyList()

        return staffArray.mapNotNull { element ->
            val staff = element.asJsonObjectOrNull() ?: return@mapNotNull null
            val id = staff.getStringOrNull("_id") ?: staff.getStringOrNull("id")
            if (id.isNullOrBlank()) {
                return@mapNotNull null
            }

            val name =
                staff.getStringOrNull("fullname")
                    ?: staff.getStringOrNull("name")
                    ?: staff.getStringOrNull("email")
                    ?: "Unnamed staff"
            val email = staff.getStringOrNull("email")

            SpinnerOption(
                id = id,
                label = if (email.isNullOrBlank() || email == name) name else "$name ($email)",
            )
        }
    }

    private fun JsonElement?.findStaffArray(preferredWrapperKey: String): JsonArray? {
        if (this == null || isJsonNull) {
            return null
        }
        if (isJsonArray) {
            return asJsonArray
        }

        val responseObject = asJsonObjectOrNull() ?: return null
        val wrapperKeys =
            listOf(preferredWrapperKey, "staff", "users", "data", "items", "results")

        return wrapperKeys
            .asSequence()
            .mapNotNull { key -> responseObject.get(key)?.takeIf { it.isJsonArray }?.asJsonArray }
            .firstOrNull()
    }

    private fun JsonElement.asJsonObjectOrNull(): JsonObject? {
        return takeIf { it.isJsonObject }?.asJsonObject
    }

    private fun JsonObject.getStringOrNull(key: String): String? {
        val value = get(key)?.takeIf { !it.isJsonNull } ?: return null
        return runCatching { value.asString }.getOrNull()?.takeIf { it.isNotBlank() }
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
