// package deakin.gopher.guardian.view.prescription
//
// import android.app.Activity
// import android.content.Intent
// import android.os.Bundle
// import android.util.Log
// import android.widget.Button
// import android.widget.EditText
// import android.widget.Toast
// import androidx.appcompat.app.AppCompatActivity
// import deakin.gopher.guardian.R
// import deakin.gopher.guardian.model.Prescription
// import deakin.gopher.guardian.model.PrescriptionItem
// import deakin.gopher.guardian.model.login.SessionManager
// import deakin.gopher.guardian.services.api.ApiClient
// import deakin.gopher.guardian.services.api.ApiService
// import retrofit2.Call
// import retrofit2.Callback
// import retrofit2.Response
//
// class IssuePrescriptionActivity : AppCompatActivity() {
//
//    private lateinit var etMedicine: EditText
//    private lateinit var etDoses: EditText
//    private lateinit var etDays: EditText
//    private lateinit var btnDone: Button
//
//    // TODO: Make this dynamic by passing patientId via Intent
//    private val apiService: ApiService by lazy { ApiClient.apiService }
//    private val patientId = "68950c034af33273204ee634"
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_issue_prescription)
//
//        etMedicine = findViewById(R.id.etMedicineName)
//        etDoses = findViewById(R.id.etDoses)
//        etDays = findViewById(R.id.etDuration)
//        btnDone = findViewById(R.id.btnIssuePrescription)
//
//        btnDone.setOnClickListener { issuePrescription() }
//    }
//
//    private fun issuePrescription() {
//        val medicine = etMedicine.text.toString().trim()
//        val doses = etDoses.text.toString().trim()
//        val days = etDays.text.toString().trim()
//
//        if (medicine.isEmpty() || doses.isEmpty() || days.isEmpty()) {
//            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val duration = days.toIntOrNull()
//        if (duration == null) {
//            Toast.makeText(this, "Duration must be a number", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val prescriptionItem = PrescriptionItem(
//            name = medicine,
//            dose = doses,
//            frequency = "Once daily",
//            durationDays = duration
//        )
//
//        val prescription = Prescription(
//            patientId = patientId,
//            items = listOf(prescriptionItem),
//            notes = ""
//        )
//
//        val token = try {
//            SessionManager.getToken()
//        } catch (e: Exception) {
//            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        // Call API to create prescription
//        apiService.createPrescription("Bearer $token", prescription)
//            .enqueue(object : Callback<Prescription> {
//                override fun onResponse(call: Call<Prescription>, response: Response<Prescription>) {
//                    if (response.isSuccessful) {
//                        Toast.makeText(
//                            this@IssuePrescriptionActivity,
//                            "Prescription issued successfully!",
//                            Toast.LENGTH_SHORT
//                        ).show()
//
//                        // Return success to previous activity
//                        val intent = Intent()
//                        intent.putExtra("patientId", patientId)
//                        setResult(Activity.RESULT_OK, intent)
//                        finish()
//                    } else {
//                        Toast.makeText(
//                            this@IssuePrescriptionActivity,
//                            "Failed to issue prescription: ${response.code()}",
//                            Toast.LENGTH_LONG
//                        ).show()
//                    }
//                }
//
//                override fun onFailure(call: Call<Prescription>, t: Throwable) {
//                    Toast.makeText(
//                        this@IssuePrescriptionActivity,
//                        "Error: ${t.localizedMessage}",
//                        Toast.LENGTH_LONG
//                    ).show()
//                }
//            })
//    }
// }

package deakin.gopher.guardian.view.prescription

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.Prescription
import deakin.gopher.guardian.model.PrescriptionItem
import deakin.gopher.guardian.model.login.SessionManager
import deakin.gopher.guardian.services.api.ApiClient
import deakin.gopher.guardian.services.api.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class IssuePrescriptionActivity : AppCompatActivity() {
    private lateinit var etMedicine: EditText
    private lateinit var etDoses: EditText
    private lateinit var etDays: EditText
    private lateinit var btnDone: Button

    private val apiService: ApiService by lazy { ApiClient.apiService }
    private val patientId = "68950c034af33273204ee634"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_issue_prescription)

        etMedicine = findViewById(R.id.etMedicineName)
        etDoses = findViewById(R.id.etDoses)
        etDays = findViewById(R.id.etDuration)
        btnDone = findViewById(R.id.btnIssuePrescription)

        btnDone.setOnClickListener { issuePrescription() }
    }

    private fun issuePrescription() {
        val medicine = etMedicine.text.toString().trim()
        val doses = etDoses.text.toString().trim()
        val days = etDays.text.toString().trim()

        if (medicine.isEmpty() || doses.isEmpty() || days.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val duration = days.toIntOrNull()
        if (duration == null) {
            Toast.makeText(this, "Duration must be a number", Toast.LENGTH_SHORT).show()
            return
        }

        val prescriptionItem =
            PrescriptionItem(
                name = medicine,
                dose = doses,
                frequency = "Once daily",
                durationDays = duration,
            )

        val prescription =
            Prescription(
                patientId = patientId,
                items = listOf(prescriptionItem),
                notes = "",
            )

        val token =
            try {
                SessionManager.getToken()
            } catch (e: Exception) {
                Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
                return
            }

        apiService.createPrescription("Bearer $token", prescription)
            .enqueue(
                object : Callback<Prescription> {
                    override fun onResponse(
                        call: Call<Prescription>,
                        response: Response<Prescription>,
                    ) {
                        if (response.isSuccessful) {
                            Toast.makeText(
                                this@IssuePrescriptionActivity,
                                "Prescription issued successfully!",
                                Toast.LENGTH_SHORT,
                            ).show()
                            finish() // Return to PrescriptionActivity, which will refresh automatically
                        } else {
                            Toast.makeText(
                                this@IssuePrescriptionActivity,
                                "Failed to issue prescription: ${response.code()}",
                                Toast.LENGTH_LONG,
                            ).show()
                        }
                    }

                    override fun onFailure(
                        call: Call<Prescription>,
                        t: Throwable,
                    ) {
                        Toast.makeText(
                            this@IssuePrescriptionActivity,
                            "Error: ${t.localizedMessage}",
                            Toast.LENGTH_LONG,
                        ).show()
                    }
                },
            )
    }
}
