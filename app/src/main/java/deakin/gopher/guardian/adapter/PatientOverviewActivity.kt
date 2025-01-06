package deakin.gopher.guardian.view.patient

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import deakin.gopher.guardian.R

class PatientOverviewActivity : AppCompatActivity() {

    // Declare image views for health metrics
    private lateinit var bloodPressureImage: ImageView
    private lateinit var glucoseLevelImage: ImageView
    private lateinit var oxygenSaturationImage: ImageView
    private lateinit var heartRateImage: ImageView
    private lateinit var pulseRateImage: ImageView
    private lateinit var respirationRateImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_overview)

        // Initialize image views
        bloodPressureImage = findViewById(R.id.image_blood_pressure)
        glucoseLevelImage = findViewById(R.id.image_glucose_level)
        oxygenSaturationImage = findViewById(R.id.image_oxygen_saturation)
        heartRateImage = findViewById(R.id.image_heart_rate)
        pulseRateImage = findViewById(R.id.image_pulse_rate)
        respirationRateImage = findViewById(R.id.image_respiration_rate)

        // Display static images for the health metrics
        displayStaticImages()
    }

    private fun displayStaticImages() {
        try {
            // Set images for each metric
            bloodPressureImage.setImageResource(R.drawable.blood_pressure_graph)
            glucoseLevelImage.setImageResource(R.drawable.glucose_level_graph)
            oxygenSaturationImage.setImageResource(R.drawable.o2_saturation_graph)
            heartRateImage.setImageResource(R.drawable.patient_temperature_graph)
            pulseRateImage.setImageResource(R.drawable.pulse_rate_graph)
            respirationRateImage.setImageResource(R.drawable.respiration_rate_graph)

            // Display a toast message for confirmation
            Toast.makeText(this, "Health data images loaded successfully.", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            // Handle any exceptions while loading images
            Toast.makeText(this, "Error loading images: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
