package deakin.gopher.guardian.view.general

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import deakin.gopher.guardian.R

class AddMedicalHistoryActivity : AppCompatActivity() {

    private lateinit var inputPatientName: EditText
    private lateinit var inputMedicalCondition: EditText
    private lateinit var inputTreatmentDetails: EditText
    private lateinit var inputDoctorNotes: EditText
    private lateinit var buttonSubmitMedicalHistory: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medical_history)

        // Initialize input fields and button
        inputPatientName = findViewById(R.id.inputPatientName)
        inputMedicalCondition = findViewById(R.id.inputMedicalCondition)
        inputTreatmentDetails = findViewById(R.id.inputTreatmentDetails)
        inputDoctorNotes = findViewById(R.id.inputDoctorNotes)
        buttonSubmitMedicalHistory = findViewById(R.id.buttonSubmitMedicalHistory)

        // Handle Submit Button Click
        buttonSubmitMedicalHistory.setOnClickListener {
            handleMedicalHistorySubmission()
        }
    }

    private fun handleMedicalHistorySubmission() {
        val patientName = inputPatientName.text.toString().trim()
        val medicalCondition = inputMedicalCondition.text.toString().trim()
        val treatmentDetails = inputTreatmentDetails.text.toString().trim()
        val doctorNotes = inputDoctorNotes.text.toString().trim()

        // Validation
        if (patientName.isEmpty() || medicalCondition.isEmpty() || treatmentDetails.isEmpty() || doctorNotes.isEmpty()) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show()
            return
        }

        // Simulate submission
        Toast.makeText(
            this,
            "Medical history added successfully:\nPatient Name: $patientName\nCondition: $medicalCondition\nTreatment: $treatmentDetails\nNotes: $doctorNotes",
            Toast.LENGTH_LONG
        ).show()

        // Clear fields after submission
        clearFields()
    }

    private fun clearFields() {
        inputPatientName.text.clear()
        inputMedicalCondition.text.clear()
        inputTreatmentDetails.text.clear()
        inputDoctorNotes.text.clear()
    }
}
