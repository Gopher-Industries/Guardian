package deakin.gopher.guardian.view.general

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import deakin.gopher.guardian.R

class LogbookActivity : AppCompatActivity() {

    private lateinit var patientSpinner: Spinner
    private lateinit var inputBP: EditText
    private lateinit var inputHR: EditText
    private lateinit var inputSleep: EditText
    private lateinit var inputWater: EditText
    private lateinit var inputBathroom: EditText
    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logbook)

        // match all IDs from XML
        patientSpinner = findViewById(R.id.patientSpinner)
        inputBP        = findViewById(R.id.inputBP)
        inputHR        = findViewById(R.id.inputHR)
        inputSleep     = findViewById(R.id.inputSleep)
        inputWater     = findViewById(R.id.inputWater)
        inputBathroom  = findViewById(R.id.inputBathroom)
        submitButton   = findViewById(R.id.submitButton)

        setupPatientSpinner()

        submitButton.setOnClickListener {
            if (validateForm()) {
                saveLog()
            }
        }
    }

    private fun setupPatientSpinner() {
        // sample data — later replace with API/DB
        val patients = listOf("Select...", "John Smith", "Jane Doe")
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            patients
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        patientSpinner.adapter = adapter
    }

    private fun validateForm(): Boolean {
        fun EditText.requireNumber(): Boolean {
            val ok = text.toString().trim().isNotEmpty()
            if (!ok) error = "Required"
            return ok
        }

        var valid = true
        valid = inputBP.requireNumber() && valid
        valid = inputHR.requireNumber() && valid
        valid = inputSleep.requireNumber() && valid
        valid = inputWater.requireNumber() && valid
        valid = inputBathroom.requireNumber() && valid

        if (patientSpinner.selectedItemPosition == 0) {
            Toast.makeText(this, "Please select a patient", Toast.LENGTH_SHORT).show()
            valid = false
        }
        return valid
    }

    private fun saveLog() {
        val patient  = patientSpinner.selectedItem.toString()
        val bp       = inputBP.text.toString().toInt()
        val hr       = inputHR.text.toString().toInt()
        val sleep    = inputSleep.text.toString().toFloat()
        val water    = inputWater.text.toString().toInt()
        val bathroom = inputBathroom.text.toString().toInt()

        // TODO: replace with API/DB
        Toast.makeText(this, "Saved log for $patient", Toast.LENGTH_SHORT).show()
    }
}
