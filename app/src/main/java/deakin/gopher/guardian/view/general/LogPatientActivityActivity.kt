package deakin.gopher.guardian.view.general

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.Patient
import deakin.gopher.guardian.model.login.SessionManager
import deakin.gopher.guardian.services.api.ApiClient
import kotlinx.coroutines.launch

class LogPatientActivityActivity : AppCompatActivity() {

    private lateinit var patientSpinner: Spinner
    private lateinit var activityTypeEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var dateEditText: EditText
    private lateinit var timeEditText: EditText
    private lateinit var submitButton: Button

    private val assignedPatients = mutableListOf<Patient>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_patient_activity)

        patientSpinner = findViewById(R.id.patientSpinner)
        activityTypeEditText = findViewById(R.id.activityTypeEditText)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        dateEditText = findViewById(R.id.dateEditText)
        timeEditText = findViewById(R.id.timeEditText)
        submitButton = findViewById(R.id.submitActivityButton)

        loadAssignedPatients()

        submitButton.setOnClickListener {
            submitActivity()
        }
    }

    private fun loadAssignedPatients() {
        lifecycleScope.launch {
            try {
                val token = SessionManager.getToken()

                val response = ApiClient.apiService.getAssignedPatients(
                    token = "Bearer $token"
                )

                if (response.isSuccessful) {
                    val patients = response.body().orEmpty()

                    assignedPatients.clear()
                    assignedPatients.addAll(patients)

                    val patientNames = mutableListOf("Select Patient")
                    patientNames.addAll(patients.map { it.fullname })

                    Toast.makeText(
                        this@LogPatientActivityActivity,
                        "Patients loaded: ${patients.size}",
                        Toast.LENGTH_LONG
                    ).show()

                    val adapter = ArrayAdapter(
                        this@LogPatientActivityActivity,
                        android.R.layout.simple_spinner_item,
                        patientNames
                    )

                    adapter.setDropDownViewResource(
                        android.R.layout.simple_spinner_dropdown_item
                    )

                    patientSpinner.adapter = adapter

                    if (patients.isEmpty()) {
                        Toast.makeText(
                            this@LogPatientActivityActivity,
                            "No assigned patients found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@LogPatientActivityActivity,
                        "Failed to load assigned patients",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@LogPatientActivityActivity,
                    "Error loading patients: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun submitActivity() {
        val selectedPosition = patientSpinner.selectedItemPosition

        val activityType = activityTypeEditText.text.toString().trim()
        val description = descriptionEditText.text.toString().trim()
        val date = dateEditText.text.toString().trim()
        val time = timeEditText.text.toString().trim()

        if (selectedPosition <= 0) {
            Toast.makeText(this, "Please select a patient", Toast.LENGTH_SHORT).show()
            return
        }

        if (activityType.isEmpty()) {
            activityTypeEditText.error = "Activity type is required"
            activityTypeEditText.requestFocus()
            return
        }

        if (description.isEmpty()) {
            descriptionEditText.error = "Description is required"
            descriptionEditText.requestFocus()
            return
        }

        if (date.isEmpty()) {
            dateEditText.error = "Date is required"
            dateEditText.requestFocus()
            return
        }

        if (time.isEmpty()) {
            timeEditText.error = "Time is required"
            timeEditText.requestFocus()
            return
        }

        val selectedPatient = assignedPatients[selectedPosition - 1]
        val timestamp = "$date $time"

        submitButton.isEnabled = false
        submitButton.text = "Submitting..."

        lifecycleScope.launch {
            try {
                val token = SessionManager.getToken()

                val response = ApiClient.apiService.logPatientActivity(
                    token = "Bearer $token",
                    patientId = selectedPatient.id,
                    activityType = activityType,
                    timestamp = timestamp,
                    comment = description
                )

                if (response.isSuccessful) {
                    Toast.makeText(
                        this@LogPatientActivityActivity,
                        "Activity logged successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    Toast.makeText(
                        this@LogPatientActivityActivity,
                        "Failed to log activity",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@LogPatientActivityActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                submitButton.isEnabled = true
                submitButton.text = "Submit Activity"
            }
        }
    }
}