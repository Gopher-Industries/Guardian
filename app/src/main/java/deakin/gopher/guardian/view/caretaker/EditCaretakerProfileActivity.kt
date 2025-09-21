//package deakin.gopher.guardian.view.caretaker
//
//import android.content.Intent
//import android.os.Bundle
//import android.widget.ImageView
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.google.android.material.button.MaterialButton
//import com.google.android.material.textfield.TextInputEditText
//import deakin.gopher.guardian.R
//import deakin.gopher.guardian.model.Caretaker
//import deakin.gopher.guardian.model.login.SessionManager
//import deakin.gopher.guardian.services.api.ApiClient
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import retrofit2.Response
//
//class EditCaretakerProfileActivity : AppCompatActivity() {
//
//    private lateinit var txtName: TextInputEditText
//    private lateinit var txtEmergencyContact: TextInputEditText
//    private lateinit var txtUnderCare: TextInputEditText
//    private lateinit var imgProfile: ImageView
//    private lateinit var btnSave: MaterialButton
//    private lateinit var backBtn: MaterialButton
//
//    private lateinit var caretaker: Caretaker
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_edit_caretakerprofile)
//
//        // Initialize views
//        txtName = findViewById(R.id.txtName)
//        txtEmergencyContact = findViewById(R.id.txtEmegencyContact)
//        txtUnderCare = findViewById(R.id.txtUnderCare)
//        imgProfile = findViewById(R.id.caretakerImage)
//        btnSave = findViewById(R.id.btnSave)
//        backBtn = findViewById(R.id.backBtn)
//
//        // Get caretaker object from intent
//        caretaker = intent.getSerializableExtra("caretaker") as Caretaker
//        populateUI(caretaker)
//
//        backBtn.setOnClickListener { finish() }
//
//        btnSave.setOnClickListener {
//            // Update the editable field
//            caretaker.fullName = txtName.text.toString()
//
//            // Call API to update caretaker profile
//            updateCaretakerProfile(caretaker)
//        }
//    }
//
//    /**
//     * Populates UI with caretaker data
//     */
//    private fun populateUI(c: Caretaker) {
//        txtName.setText(c.fullName ?: "")
//        txtEmergencyContact.setText(c.email ?: "")
//        txtUnderCare.setText(c.assignedPatients?.size?.toString() ?: "0")
//    }
//
//    /**
//     * Updates caretaker profile on the backend
//     */
//    private fun updateCaretakerProfile(caretaker: Caretaker) {
//        // Get token from SessionManager
//        val token = try {
//            "Bearer ${SessionManager.getToken()}"
//        } catch (e: Exception) {
//            Toast.makeText(this, "Token not found. Please login again.", Toast.LENGTH_LONG).show()
//            return
//        }
//
//        // Request body with only editable fields
//        val requestBody = mapOf(
//            "caretakerId" to (caretaker.id ?: ""),
//            "fullname" to (caretaker.fullName ?: "")
//        )
//
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                val response: Response<Void> = ApiClient.apiService.updateCaretakerProfile(token, requestBody)
//                if (response.isSuccessful) {
//                    // Return updated caretaker to profile screen
//                    val resultIntent = Intent()
//                    resultIntent.putExtra("updatedCaretaker", caretaker)
//                    setResult(RESULT_OK, resultIntent)
//
//                    runOnUiThread {
//                        Toast.makeText(
//                            this@EditCaretakerProfileActivity,
//                            "Profile updated successfully",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//
//                    finish()
//                } else {
//                    runOnUiThread {
//                        Toast.makeText(
//                            this@EditCaretakerProfileActivity,
//                            "Update failed: ${response.code()}",
//                            Toast.LENGTH_LONG
//                        ).show()
//                    }
//                }
//            } catch (e: Exception) {
//                runOnUiThread {
//                    Toast.makeText(
//                        this@EditCaretakerProfileActivity,
//                        "Error: ${e.message}",
//                        Toast.LENGTH_LONG
//                    ).show()
//                }
//            }
//        }
//    }
//}

package deakin.gopher.guardian.view.caretaker

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.Caretaker
import deakin.gopher.guardian.model.login.SessionManager
import deakin.gopher.guardian.services.api.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class EditCaretakerProfileActivity : AppCompatActivity() {

    private lateinit var txtName: TextInputEditText
    private lateinit var txtAddress: TextInputEditText
    private lateinit var txtDoB: TextInputEditText
    private lateinit var txtPhone: TextInputEditText
    private lateinit var txtUnderCare: TextInputEditText
    private lateinit var txtMedicareNumber: TextInputEditText
    private lateinit var txtEmail: TextInputEditText
    private lateinit var btnSave: MaterialButton
    private lateinit var backBtn: MaterialButton

    private lateinit var caretaker: Caretaker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_caretakerprofile)

        // Initialize fields
        txtName = findViewById(R.id.txtName)
        txtAddress = findViewById(R.id.txtAddress)
        txtDoB = findViewById(R.id.txtDoB)
        txtPhone = findViewById(R.id.txtPhone)
        txtUnderCare = findViewById(R.id.txtUnderCare)
        txtMedicareNumber = findViewById(R.id.txtMedicareNumber)
        txtEmail = findViewById(R.id.txtEmail)
        btnSave = findViewById(R.id.btnSave)
        backBtn = findViewById(R.id.backBtn)

        caretaker = intent.getSerializableExtra("caretaker") as Caretaker
        populateUI(caretaker)

        backBtn.setOnClickListener { finish() }

        btnSave.setOnClickListener {
            // Update caretaker object with new values from input fields
            caretaker.apply {
                fullName = txtName.text.toString().trim()
                address = txtAddress.text.toString().trim()
                dob = txtDoB.text.toString().trim()
                phone = txtPhone.text.toString().trim()
                ward = txtUnderCare.text.toString().trim()
                medicareNumber = txtMedicareNumber.text.toString().trim()
                email = txtEmail.text.toString().trim()
            }

            // Validate at least name and phone
            if (caretaker.fullName.isNullOrEmpty()) {
                Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            updateCaretakerProfile(caretaker)
        }
    }

    private fun populateUI(c: Caretaker) {
        txtName.setText(c.fullName ?: "")
        txtAddress.setText(c.address ?: "")
        txtDoB.setText(c.dob ?: "")
        txtPhone.setText(c.phone ?: "")
        txtUnderCare.setText(c.ward ?: "")
        txtMedicareNumber.setText(c.medicareNumber ?: "")
        txtEmail.setText(c.email ?: "")

        // All fields editable
        txtName.isEnabled = true
        txtAddress.isEnabled = true
        txtDoB.isEnabled = true
        txtPhone.isEnabled = true
        txtUnderCare.isEnabled = true
        txtMedicareNumber.isEnabled = true
        txtEmail.isEnabled = true
    }

    private fun updateCaretakerProfile(caretaker: Caretaker) {
        val token = try {
            "Bearer ${SessionManager.getToken()}"
        } catch (e: Exception) {
            Toast.makeText(this, "Token not found. Please login again.", Toast.LENGTH_LONG).show()
            return
        }

        val requestBody = mapOf(
            "caretakerId" to (caretaker.id ?: ""),
            "fullname" to (caretaker.fullName ?: ""),
            "address" to (caretaker.address ?: ""),
            "dob" to (caretaker.dob ?: ""),
            "phone" to (caretaker.phone ?: ""),
            "ward" to (caretaker.ward ?: ""),
            "medicareNumber" to (caretaker.medicareNumber ?: ""),
            "email" to (caretaker.email ?: "")
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response: Response<Void> = ApiClient.apiService.updateCaretakerProfile(token, requestBody)
                if (response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(this@EditCaretakerProfileActivity, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    }

                    // Return updated caretaker back to profile screen
                    val resultIntent = Intent()
                    resultIntent.putExtra("updatedCaretaker", caretaker)
                    setResult(RESULT_OK, resultIntent)
                    finish()
                } else {
                    runOnUiThread {
                        Toast.makeText(
                            this@EditCaretakerProfileActivity,
                            "Update failed: ${response.code()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this@EditCaretakerProfileActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}

