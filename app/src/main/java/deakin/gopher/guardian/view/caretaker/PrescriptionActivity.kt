package deakin.gopher.guardian.view.caretaker

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import deakin.gopher.guardian.R

class PrescriptionActivity : AppCompatActivity() {
    private lateinit var patientNameEditText: EditText
    private lateinit var patientDobEditText: EditText
    private lateinit var patientGenderEditText: EditText
    private lateinit var drugNameEditText: EditText
    private lateinit var dosageEditText: EditText
    private lateinit var durationEditText: EditText
    private lateinit var instructionsEditText: EditText
    private lateinit var checkPrescriptionButton: Button
    private lateinit var assignPrescriptionButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prescription)

        // Initialize views
        patientNameEditText = findViewById(R.id.etPatientName)
        patientDobEditText = findViewById(R.id.etPatientDob)
        patientGenderEditText = findViewById(R.id.etPatientGender)
        drugNameEditText = findViewById(R.id.etDrugName)
        dosageEditText = findViewById(R.id.etDosage)
        durationEditText = findViewById(R.id.etDuration)
        instructionsEditText = findViewById(R.id.etInstructions)
        checkPrescriptionButton = findViewById(R.id.btnCheckPrescription)
        assignPrescriptionButton = findViewById(R.id.btnAssignPrescription)

        // Button listeners
        checkPrescriptionButton.setOnClickListener {
            // Placeholder logic for checking prescription
            Toast.makeText(this, "Checking existing prescriptions...", Toast.LENGTH_SHORT).show()
        }

        assignPrescriptionButton.setOnClickListener {
            val patientName = patientNameEditText.text.toString().trim()
            val drugName = drugNameEditText.text.toString().trim()

            if (patientName.isEmpty() || drugName.isEmpty()) {
                Toast.makeText(this, "Please fill all mandatory fields", Toast.LENGTH_SHORT).show()
            } else {
                // Placeholder for saving prescription
                Toast.makeText(
                    this,
                    "Prescription assigned to $patientName for $drugName",
                    Toast.LENGTH_LONG,
                ).show()
            }
        }
    }
}
