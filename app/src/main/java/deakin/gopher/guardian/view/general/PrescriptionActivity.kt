package deakin.gopher.guardian.view.general

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.login.SessionManager
import deakin.gopher.guardian.services.api.ApiClient
import kotlinx.coroutines.launch
import org.json.JSONObject

class PrescriptionActivity : AppCompatActivity() {
    private lateinit var resultText: TextView
    private lateinit var prescriptionIdInput: EditText

    private val patientIds = listOf(
        "6a05a5ca0887fc839270495d",
        "6a05a5ca0887fc839270495e",
        "6a05a5ca0887fc839270495f",
        "69dca7f5e3887dde19d9f6e2"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prescription)

        resultText = findViewById(R.id.resultText)
        prescriptionIdInput = findViewById(R.id.prescriptionIdInput)

        findViewById<Button>(R.id.fetchButton).setOnClickListener {
            fetchPrescriptions()
        }

        findViewById<Button>(R.id.discontinueButton).setOnClickListener {
            confirmDiscontinue()
        }

        findViewById<Button>(R.id.deleteButton).setOnClickListener {
            confirmDelete()
        }

        fetchPrescriptions()
    }

    private fun fetchPrescriptions() {
        lifecycleScope.launch {
            try {
                resultText.text = "Loading prescriptions..."

                val token = "Bearer ${SessionManager.getToken()}"
                val output = StringBuilder()

                output.append("Prescriptions for all assigned patient\n\n")

                for (patientId in patientIds) {
                    val response =
                        ApiClient.apiService.getPrescriptionsForPatient(
                            token = token,
                            patientId = patientId
                        )

                    if (response.isSuccessful) {
                        val json = response.body()?.string() ?: ""
                        val root = JSONObject(json)
                        val prescriptions = root.getJSONArray("prescriptions")

                        if (prescriptions.length() == 0) {
                            output.append("Patient ID: $patientId\n")
                            output.append("No prescriptions found.\n\n")
                        } else {
                            for (i in 0 until prescriptions.length()) {
                                val prescription = prescriptions.getJSONObject(i)
                                val items = prescription.getJSONArray("items")
                                val medicine = items.getJSONObject(0)

                                val patientName =
                                    if (prescription.get("patient") is JSONObject) {
                                        prescription.getJSONObject("patient")
                                            .optString("fullname", "Unknown Patient")
                                    } else {
                                        "Patient ID: ${prescription.getString("patient")}"
                                    }

                                output.append("Patient: $patientName\n")
                                output.append("Patient ID: $patientId\n")
                                output.append("Prescription ID: ${prescription.getString("_id")}\n")
                                output.append("Medication: ${medicine.getString("name")}\n")
                                output.append("Dose: ${medicine.getString("dose")}\n")
                                output.append("Frequency: ${medicine.getString("frequency")}\n")
                                output.append("Duration: ${medicine.getInt("durationDays")} days\n")
                                output.append("Status: ${prescription.getString("status").uppercase()}\n")
                                output.append("----------------------------\n\n")
                            }
                        }
                    } else {
                        output.append("Patient ID: $patientId\n")
                        output.append("Failed to fetch prescriptions. Code: ${response.code()}\n\n")
                    }
                }

                resultText.text = output.toString()

                Toast.makeText(
                    this@PrescriptionActivity,
                    "Prescriptions loaded",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                resultText.text = "Error occurred:\n${e.message}"
            }
        }
    }

    private fun confirmDiscontinue() {
        val prescriptionId = prescriptionIdInput.text.toString().trim()

        if (prescriptionId.isEmpty()) {
            Toast.makeText(this, "Enter prescription ID first", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Discontinue Prescription")
            .setMessage("Are you sure you want to discontinue this prescription?\n\n$prescriptionId")
            .setPositiveButton("Discontinue") { _, _ ->
                discontinuePrescription(prescriptionId)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun confirmDelete() {
        val prescriptionId = prescriptionIdInput.text.toString().trim()

        if (prescriptionId.isEmpty()) {
            Toast.makeText(this, "Enter prescription ID first", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Delete Prescription")
            .setMessage("Are you sure you want to delete this prescription?\n\n$prescriptionId\n\nThis cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                deletePrescription(prescriptionId)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun discontinuePrescription(prescriptionId: String) {
        lifecycleScope.launch {
            try {
                resultText.text = "Discontinuing prescription..."

                val token = "Bearer ${SessionManager.getToken()}"

                val response =
                    ApiClient.apiService.discontinuePrescription(
                        token = token,
                        prescriptionId = prescriptionId
                    )

                if (response.isSuccessful) {
                    Toast.makeText(
                        this@PrescriptionActivity,
                        "Prescription discontinued",
                        Toast.LENGTH_SHORT
                    ).show()

                    fetchPrescriptions()
                } else {
                    resultText.text =
                        "Failed to discontinue prescription.\nCode: ${response.code()}\nMessage: ${response.message()}"
                }
            } catch (e: Exception) {
                resultText.text = "Error occurred:\n${e.message}"
            }
        }
    }

    private fun deletePrescription(prescriptionId: String) {
        lifecycleScope.launch {
            try {
                resultText.text = "Deleting prescription..."

                val token = "Bearer ${SessionManager.getToken()}"

                val response =
                    ApiClient.apiService.deletePrescription(
                        token = token,
                        prescriptionId = prescriptionId
                    )

                if (response.isSuccessful) {
                    Toast.makeText(
                        this@PrescriptionActivity,
                        "Prescription deleted",
                        Toast.LENGTH_SHORT
                    ).show()

                    prescriptionIdInput.text.clear()
                    fetchPrescriptions()
                } else {
                    resultText.text =
                        "Failed to delete prescription.\nCode: ${response.code()}\nMessage: ${response.message()}"
                }
            } catch (e: Exception) {
                resultText.text = "Error occurred:\n${e.message}"
            }
        }
    }
}