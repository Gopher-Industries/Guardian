package deakin.gopher.guardian.view.patient

// Core Android Components:
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

// GraphView:
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries

import com.google.firebase.database.FirebaseDatabase
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.Patient
<<<<<<< HEAD
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
=======

class PatientOverviewActivity : AppCompatActivity() {
    private lateinit var patient: Patient
    private lateinit var healthDataTextView: TextView
    private lateinit var taskHistoryTextView: TextView
    private lateinit var progressChart: GraphView  // For visualization
>>>>>>> 4a7fb6e1ccd9a05f8a3a7f255dcb3c26923860d4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_overview)

        // Initialize views
        healthDataTextView = findViewById(R.id.health_data_text)
        taskHistoryTextView = findViewById(R.id.task_history_text)
        progressChart = findViewById(R.id.progress_chart)

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
<<<<<<< HEAD
            displayStaticImages() // Display static images for now
=======
>>>>>>> 4a7fb6e1ccd9a05f8a3a7f255dcb3c26923860d4
        } else {
            Toast.makeText(this, "Patient ID is missing", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchPatientData(patientId: String) {
<<<<<<< HEAD
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


=======
        val patientRef = FirebaseDatabase.getInstance().getReference("patients").child(patientId)
        patientRef.get().addOnSuccessListener { snapshot ->
            patient = snapshot.getValue(Patient::class.java)!!
            displayData()
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Failed to load patient data: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun displayData() {
        // Display health data
        healthDataTextView.text = formatHealthData(patient.healthData)

        // Display task history
        taskHistoryTextView.text = patient.taskHistory.joinToString("\n")

        // Load care plan progress into chart
        loadProgressChart(patient.carePlanProgress)
    }

    private fun formatHealthData(healthData: Map<String, Any>): String {
        return healthData.entries.joinToString("\n") { "${it.key}: ${it.value}" }
    }

    private fun loadProgressChart(progressData: Map<String, Any>) {
        // Explicitly specify the type of entry to avoid inference issues
        val dataPoints = progressData.entries.mapIndexed { index, entry ->
            val value = (entry.value as? Number)?.toDouble() ?: 0.0  // Safely cast to Double
            DataPoint(index.toDouble(), value)  // Create a DataPoint object for each progress point
        }

        // Create a LineGraphSeries and set it to the GraphView
        val series = LineGraphSeries(dataPoints.toTypedArray())
        series.color = resources.getColor(R.color.blue)  // Set color for the line graph

        // Set the series to the chart
        progressChart.addSeries(series)
    }
}
>>>>>>> 4a7fb6e1ccd9a05f8a3a7f255dcb3c26923860d4
