@file:Suppress("ktlint")

package deakin.gopher.guardian.view.general

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import deakin.gopher.guardian.databinding.ActivityEditPatientBinding
import deakin.gopher.guardian.model.Patient
import deakin.gopher.guardian.model.login.SessionManager
import deakin.gopher.guardian.services.api.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

// DTO for update request (matches API spec)
data class UpdatePatientRequest(
    val fullname: String,
    val dateOfBirth: String,
    val gender: String
)

class EditPatientActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditPatientBinding
    private lateinit var patient: Patient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPatientBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get patient from Intent
        patient = intent.getSerializableExtra("patient") as Patient

        // Pre-fill fields with current data
        binding.etFullname.setText(patient.fullname)
        binding.etDateOfBirth.setText(patient.dateOfBirth)
        binding.etGender.setText(patient.gender)

        // Save button
        binding.btnSave.setOnClickListener {
            updatePatient()
        }
    }

    private fun updatePatient() {
        val updatedPatient = UpdatePatientRequest(
            fullname = binding.etFullname.text.toString().trim(),
            dateOfBirth = binding.etDateOfBirth.text.toString().trim(),
            gender = binding.etGender.text.toString().trim()
        )

        val token = "Bearer ${SessionManager.getToken()}"

        CoroutineScope(Dispatchers.IO).launch {
            val response = try {
                ApiClient.apiService.updatePatient(
                    token = token,
                    patientId = patient.id,      // 👈 ensure this is the DB ObjectId
                    request = updatedPatient
                )
            } catch (e: Exception) {
                null
            }

            withContext(Dispatchers.Main) {
                if (response?.isSuccessful == true) {
                    Toast.makeText(this@EditPatientActivity, "Patient updated successfully", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(
                        this@EditPatientActivity,
                        "Update failed: ${response?.errorBody()?.string() ?: response?.message() ?: "Unknown"}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}