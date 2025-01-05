package deakin.gopher.guardian.view.patient

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.Patient
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PatientOverviewActivity : AppCompatActivity() {

    private lateinit var healthDataTextView: TextView
    private lateinit var taskHistoryTextView: TextView
    private lateinit var apiDataTextView: TextView

    private lateinit var bloodPressureImage: ImageView
    private lateinit var glucoseLevelImage: ImageView
    private lateinit var oxygenSaturationImage: ImageView
    private lateinit var heartRateImage: ImageView
    private lateinit var pulseRateImage: ImageView
    private lateinit var respirationRateImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_overview)

        // Initialize views
        healthDataTextView = findViewById(R.id.health_data_text)
        taskHistoryTextView = findViewById(R.id.task_history_text)
        apiDataTextView = findViewById(R.id.api_data_text)

        // Initialize image views
        bloodPressureImage = findViewById(R.id.image_blood_pressure)
        glucoseLevelImage = findViewById(R.id.image_glucose_level)
        oxygenSaturationImage = findViewById(R.id.image_oxygen_saturation)
        heartRateImage = findViewById(R.id.image_heart_rate)
        pulseRateImage = findViewById(R.id.image_pulse_rate)
        respirationRateImage = findViewById(R.id.image_respiration_rate)

        val patientId = intent.getStringExtra("PATIENT_ID")
        if (patientId != null) {
            fetchPatientData(patientId)
            displayStaticImages() // Display static images for now
        } else {
            Toast.makeText(this, "Patient ID is missing", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchPatientData(patientId: String) {
        val patient = Patient(patientId = patientId, firstName = "Olivia", lastName = "Barton")
        patient.dob = "1990-06-18"

        displayPatientData(patient)
    }

    private fun displayPatientData(patient: Patient) {
        val age = calculateAge(patient.dob)
        healthDataTextView.text = "Health Data:\nName: ${patient.firstName} ${patient.lastName}\nAge: $age"
        taskHistoryTextView.text = "Task History:\n1. Complete Exercise Routine\n2. Follow-up Appointment"
    }

    private fun calculateAge(dob: String?): Int {
        if (dob.isNullOrEmpty()) return 0
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val birthDate = sdf.parse(dob) ?: return 0
        val calendar = Calendar.getInstance().apply { time = birthDate }
        val now = Calendar.getInstance()
        var age = now.get(Calendar.YEAR) - calendar.get(Calendar.YEAR)
        if (now.get(Calendar.DAY_OF_YEAR) < calendar.get(Calendar.DAY_OF_YEAR)) age--
        return age
    }

    private fun displayStaticImages() {
        // Set static images for each metric
        bloodPressureImage.setImageResource(R.drawable.blood_pressure_graph)
        glucoseLevelImage.setImageResource(R.drawable.glucose_level_graph)
        oxygenSaturationImage.setImageResource(R.drawable.o2_saturation_graph)
        heartRateImage.setImageResource(R.drawable.patient_temperature_graph)
        pulseRateImage.setImageResource(R.drawable.pulse_rate_graph)
        respirationRateImage.setImageResource(R.drawable.respiration_rate_graph)

        // Update API status
        apiDataTextView.text = "Health data loaded."
    }
}


