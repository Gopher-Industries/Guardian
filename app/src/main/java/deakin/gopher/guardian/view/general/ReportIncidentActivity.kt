package deakin.gopher.guardian.view.general

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import deakin.gopher.guardian.R

class ReportIncidentActivity : AppCompatActivity() {

    private lateinit var inputIncidentTitle: EditText
    private lateinit var inputIncidentDescription: EditText
    private lateinit var inputIncidentDate: EditText
    private lateinit var inputIncidentLocation: EditText
    private lateinit var buttonSubmitIncident: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_incident)

        // Initialize input fields and button
        inputIncidentTitle = findViewById(R.id.inputIncidentTitle)
        inputIncidentDescription = findViewById(R.id.inputIncidentDescription)
        inputIncidentDate = findViewById(R.id.inputIncidentDate)
        inputIncidentLocation = findViewById(R.id.inputIncidentLocation)
        buttonSubmitIncident = findViewById(R.id.buttonSubmitIncident)

        // Handle Submit Incident Button Click
        buttonSubmitIncident.setOnClickListener {
            handleSubmitIncident()
        }
    }

    private fun handleSubmitIncident() {
        val title = inputIncidentTitle.text.toString().trim()
        val description = inputIncidentDescription.text.toString().trim()
        val date = inputIncidentDate.text.toString().trim()
        val location = inputIncidentLocation.text.toString().trim()

        // Validation
        if (title.isEmpty() || description.isEmpty() || date.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show()
            return
        }

        // Simulate submission
        Toast.makeText(
            this,
            "Incident reported successfully:\nTitle: $title\nDescription: $description\nDate: $date\nLocation: $location",
            Toast.LENGTH_LONG
        ).show()

        // Clear fields after submission
        clearFields()
    }

    private fun clearFields() {
        inputIncidentTitle.text.clear()
        inputIncidentDescription.text.clear()
        inputIncidentDate.text.clear()
        inputIncidentLocation.text.clear()
    }
}
