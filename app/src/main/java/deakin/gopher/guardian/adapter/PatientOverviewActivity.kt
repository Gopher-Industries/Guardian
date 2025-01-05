package deakin.gopher.guardian.view.patient

// Core Android Components:
import android.os.Bundle
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

class PatientOverviewActivity : AppCompatActivity() {
    private lateinit var patient: Patient
    private lateinit var healthDataTextView: TextView
    private lateinit var taskHistoryTextView: TextView
    private lateinit var progressChart: GraphView  // For visualization

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_overview)

        // Initialize views
        healthDataTextView = findViewById(R.id.health_data_text)
        taskHistoryTextView = findViewById(R.id.task_history_text)
        progressChart = findViewById(R.id.progress_chart)

        val patientId = intent.getStringExtra("PATIENT_ID")
        if (patientId != null) {
            fetchPatientData(patientId)
        } else {
            Toast.makeText(this, "Patient ID is missing", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchPatientData(patientId: String) {
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
