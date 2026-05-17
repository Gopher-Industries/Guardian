package deakin.gopher.guardian.view.general

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.gson.Gson
import deakin.gopher.guardian.databinding.ActivityAddEditPrescriptionBinding
import deakin.gopher.guardian.model.ApiErrorResponse
import deakin.gopher.guardian.model.CreatePrescriptionRequest
import deakin.gopher.guardian.model.Prescription
import deakin.gopher.guardian.model.PrescriptionItemRequest
import deakin.gopher.guardian.model.UpdatePrescriptionRequest
import deakin.gopher.guardian.model.login.SessionManager
import deakin.gopher.guardian.services.api.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import deakin.gopher.guardian.model.login.Role


class AddEditPrescriptionActivity : BaseActivity() {

    private lateinit var binding: ActivityAddEditPrescriptionBinding

    private var patientId: String = ""
    private var patientName: String = ""
    private var existingPrescription: Prescription? = null

    private val isEditMode: Boolean
        get() = existingPrescription != null

    companion object {
        const val EXTRA_PATIENT_ID = "extra_patient_id"
        const val EXTRA_PATIENT_NAME = "extra_patient_name"
        const val EXTRA_PRESCRIPTION = "extra_prescription"
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditPrescriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currentUser = SessionManager.getCurrentUser()

        if (currentUser.role != Role.Doctor) {
            Toast.makeText(
                this, "Only doctor can add or edit prescription", Toast.LENGTH_SHORT
            ).show()
            finish()
            return
        }

        patientId = intent.getStringExtra(EXTRA_PATIENT_ID).orEmpty()
        patientName = intent.getStringExtra(EXTRA_PATIENT_NAME).orEmpty()

        @Suppress("DEPRECATION") existingPrescription =
            intent.getSerializableExtra(EXTRA_PRESCRIPTION) as? Prescription

        if (patientId.isBlank()) {
            showMessage("Patient ID missing")
            finish()
            return
        }

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.toolbar.title = if (isEditMode) {
            "Edit Prescription"
        } else {
            "Add Prescription"
        }

        binding.tvPatientName.text = if (patientName.isNotBlank()) {
            "Patient: $patientName"
        } else {
            "Patient ID: $patientId"
        }

        binding.btnSavePrescription.text = if (isEditMode) {
            "Update Prescription"
        } else {
            "Create Prescription"
        }

        prefillPrescription()

        binding.btnSavePrescription.setOnClickListener {
            validateAndSubmit()
        }
    }

    private fun prefillPrescription() {
        val firstItem = existingPrescription?.items?.firstOrNull() ?: return

        binding.etMedicineName.setText(firstItem.name)
        binding.etDose.setText(firstItem.dose)
        binding.etFrequency.setText(firstItem.frequency)
        binding.etDurationDays.setText(firstItem.durationDays.toString())
    }

    private fun validateAndSubmit() {
        clearErrors()

        val medicineName = binding.etMedicineName.text.toString().trim()
        val dose = binding.etDose.text.toString().trim()
        val frequency = binding.etFrequency.text.toString().trim()
        val durationText = binding.etDurationDays.text.toString().trim()

        var hasError = false

        if (medicineName.isBlank()) {
            binding.tilMedicineName.error = "Medicine name is required"
            hasError = true
        }

        if (dose.isBlank()) {
            binding.tilDose.error = "Dose is required"
            hasError = true
        }

        if (frequency.isBlank()) {
            binding.tilFrequency.error = "Frequency is required"
            hasError = true
        }

        val durationDays = durationText.toIntOrNull()
        if (durationText.isBlank()) {
            binding.tilDurationDays.error = "Duration is required"
            hasError = true
        } else if (durationDays == null || durationDays <= 0) {
            binding.tilDurationDays.error = "Enter a valid number of days"
            hasError = true
        }

        if (hasError || durationDays == null) {
            return
        }

        val item = PrescriptionItemRequest(
            name = medicineName, dose = dose, frequency = frequency, durationDays = durationDays
        )

        if (isEditMode) {
            updatePrescription(item)
        } else {
            createPrescription(item)
        }
    }

    private fun createPrescription(item: PrescriptionItemRequest) {
        val token = "Bearer ${SessionManager.getToken()}"

        val request = CreatePrescriptionRequest(
            patientId = patientId, items = listOf(item)
        )

        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                setLoading(true)
            }

            val response = try {
                ApiClient.apiService.createPrescription(token, request)
            } catch (e: Exception) {
                null
            }

            withContext(Dispatchers.Main) {
                setLoading(false)

                if (response?.isSuccessful == true) {
                    showMessage("Prescription created successfully")
                    finish()
                } else {
                    showMessage(getErrorMessage(response, "Failed to create prescription"))
                }
            }
        }
    }

    private fun updatePrescription(item: PrescriptionItemRequest) {
        val prescriptionId = existingPrescription?.id

        if (prescriptionId.isNullOrBlank()) {
            showMessage("Prescription ID missing")
            return
        }

        val token = "Bearer ${SessionManager.getToken()}"

        val request = UpdatePrescriptionRequest(
            patientId = patientId, items = listOf(item)
        )

        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                setLoading(true)
            }

            val response = try {
                ApiClient.apiService.updatePrescription(token, prescriptionId, request)
            } catch (e: Exception) {
                null
            }

            withContext(Dispatchers.Main) {
                setLoading(false)

                if (response?.isSuccessful == true) {
                    showMessage("Prescription updated successfully")
                    finish()
                } else {
                    showMessage(getErrorMessage(response, "Failed to update prescription"))
                }
            }
        }
    }

    private fun clearErrors() {
        binding.tilMedicineName.error = null
        binding.tilDose.error = null
        binding.tilFrequency.error = null
        binding.tilDurationDays.error = null
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE

        binding.btnSavePrescription.isEnabled = !isLoading
        binding.etMedicineName.isEnabled = !isLoading
        binding.etDose.isEnabled = !isLoading
        binding.etFrequency.isEnabled = !isLoading
        binding.etDurationDays.isEnabled = !isLoading
    }

    private fun getErrorMessage(response: Response<*>?, fallbackMessage: String): String {
        val rawErrorBody = try {
            response?.errorBody()?.string()
        } catch (e: Exception) {
            null
        }

        val errorResponse = try {
            Gson().fromJson(rawErrorBody, ApiErrorResponse::class.java)
        } catch (e: Exception) {
            null
        }

        return errorResponse?.apiError?.takeIf { it.isNotBlank() }
            ?: rawErrorBody?.takeIf { it.isNotBlank() } ?: response?.message()
                ?.takeIf { it.isNotBlank() } ?: fallbackMessage
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}