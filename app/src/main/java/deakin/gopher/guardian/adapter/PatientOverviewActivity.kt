package deakin.gopher.guardian.view.patient

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.Patient
import java.text.SimpleDateFormat
import java.util.*

class PatientOverviewActivity : AppCompatActivity() {
    private lateinit var healthDataTextView: TextView
    private lateinit var taskHistoryTextView: TextView
    private lateinit var apiDataTextView: TextView // For API Data

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_overview)

        // Initialize views
        healthDataTextView = findViewById(R.id.health_data_text)
        taskHistoryTextView = findViewById(R.id.task_history_text)
        apiDataTextView = findViewById(R.id.api_data_text)

        val patientId = intent.getStringExtra("PATIENT_ID")
        if (patientId != null) {
            fetchPatientData(patientId)
            fetchApiData()  // Fetch data from the API
        } else {
            Toast.makeText(this, "Patient ID is missing", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchPatientData(patientId: String) {
        // Here, you would normally fetch data from Firebase
        // Just a sample patient object
        val patient = Patient(patientId, "John", "Doe")

        // Simulate patient data
        patient.dob = "1994-05-16" // Example date of birth in YYYY-MM-DD format

        displayData(patient)
    }

    private fun displayData(patient: Patient) {
        // Calculate age based on dob
        val age = calculateAge(patient.dob)

        // Display health data and task history
        healthDataTextView.text = "Health Data:\nName: ${patient.firstName} ${patient.lastName}\nAge: $age"
        taskHistoryTextView.text = "Task History: \nComplete exercise routine."
    }

    private fun calculateAge(dob: String?): Int {
        if (dob == null) return 0

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val birthDate = sdf.parse(dob)
        val calendar = Calendar.getInstance()
        calendar.time = birthDate

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val birthYear = calendar.get(Calendar.YEAR)

        var age = currentYear - birthYear
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
        val birthMonth = calendar.get(Calendar.MONTH)

        // Adjust age if the birthday hasn't occurred yet this year
        if (currentMonth < birthMonth || (currentMonth == birthMonth && Calendar.getInstance().get(Calendar.DAY_OF_MONTH) < calendar.get(Calendar.DAY_OF_MONTH))) {
            age--
        }

        return age
    }

    private fun fetchApiData() {
        // Simulate API call
        apiDataTextView.text = "Fetching data..."

        // Example API data simulation
        Handler(Looper.getMainLooper()).postDelayed({
            val receivedData = "API: Blood Pressure: 120/80\nHeart Rate: 72 bpm"
            apiDataTextView.text = receivedData
        }, 2000) // Simulate a delay for network request
    }
}