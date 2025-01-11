package deakin.gopher.guardian.view.general

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import deakin.gopher.guardian.R
import deakin.gopher.guardian.view.patient.MetricDetailActivity

class HealthDataActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_data)
        // Retrieve patient details from the intent
        val patientId = intent.getStringExtra("patientId")
        val firstName = intent.getStringExtra("firstName")
        val middleName = intent.getStringExtra("middleName")
        val lastName = intent.getStringExtra("lastName")

        // Update UI with patient details
        findViewById<TextView>(R.id.patient_overview_name).text = "Name: $firstName $middleName $lastName"
        findViewById<TextView>(R.id.patient_overview_id).text = "Patient ID: $patientId"
    }

    // Existing metric click handling
    fun onMetricClick(view: View, title: String, image: Int, description: String) {
        val intent = Intent(this, MetricDetailActivity::class.java).apply {
            putExtra("METRIC_TITLE", title)
            putExtra("METRIC_IMAGE", image)
            putExtra("METRIC_DESCRIPTION", description)
        }
        startActivity(intent)
    }

    fun onBloodPressureClick(view: View) {
        onMetricClick(view, "Blood Pressure", R.drawable.blood_pressure_graph, "Blood pressure helps monitor your cardiovascular health.")
    }

    fun onOxygenSaturationClick(view: View) {
        onMetricClick(view, "Oxygen Saturation", R.drawable.o2_saturation_graph, "Oxygen saturation measures the oxygen levels in your blood.")
    }

    fun onGlucoseLevelClick(view: View) {
        onMetricClick(view, "Glucose Level", R.drawable.glucose_level_graph, "Glucose levels help monitor your blood sugar levels.")
    }

    fun onPulseRateClick(view: View) {
        onMetricClick(view, "Pulse Rate", R.drawable.pulse_rate_graph, "Pulse rate indicates your heartbeats per minute.")
    }

    fun onRespirationRateClick(view: View) {
        onMetricClick(view, "Respiration Rate", R.drawable.respiration_rate_graph, "Respiration rate measures the number of breaths per minute.")
    }

    fun onPatientTempClick(view: View) {
        onMetricClick(view, "Patient Temperature", R.drawable.patient_temperature_graph, "Body temperature helps monitor for fever or hypothermia.")
    }
}
