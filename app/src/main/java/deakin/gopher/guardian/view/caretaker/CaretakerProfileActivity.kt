//
//
// package deakin.gopher.guardian.view.caretaker
//
// import android.os.Bundle
// import android.util.Log
// import android.view.View
// import android.widget.TextView
// import android.widget.Toast
// import androidx.appcompat.app.AppCompatActivity
// import androidx.lifecycle.lifecycleScope
// import com.google.android.material.button.MaterialButton
// import com.google.android.material.textfield.TextInputEditText
// import deakin.gopher.guardian.R
// import deakin.gopher.guardian.model.Caretaker
// import deakin.gopher.guardian.model.login.SessionManager
// import deakin.gopher.guardian.services.api.ApiClient
// import kotlinx.coroutines.launch
// import retrofit2.HttpException
//
// class CaretakerProfileActivity : AppCompatActivity() {
//
//    private lateinit var txtName: TextInputEditText
//    private lateinit var txtAddress: TextInputEditText
//    private lateinit var txtDoB: TextInputEditText
//    private lateinit var txtPhone: TextInputEditText
//    private lateinit var txtUnderCare: TextInputEditText
//    private lateinit var txtMedicareNumber: TextInputEditText
//    private lateinit var txtEmergencyContact: TextInputEditText
//    private lateinit var backBtn: MaterialButton
//    private lateinit var txtLoading: TextView
//
//    private lateinit var caretaker: Caretaker
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_caretakerprofile)
//
//        // Initialize views
//        txtName = findViewById(R.id.txtName)
//        txtAddress = findViewById(R.id.txtAddress)
//        txtDoB = findViewById(R.id.txtDoB)
//        txtPhone = findViewById(R.id.txtPhone)
//        txtUnderCare = findViewById(R.id.txtUnderCare)
//        txtMedicareNumber = findViewById(R.id.txtMedicareNumber)
//        txtEmergencyContact = findViewById(R.id.txtEmegencyContact)
//        backBtn = findViewById(R.id.backBtn)
//        txtLoading = findViewById(R.id.txtLoading)
//
//        backBtn.setOnClickListener { finish() }
//
//        // Show loading
//        txtLoading.visibility = View.VISIBLE
//
//        // Log and display session data
//        logSessionData()
//
//        // Fetch profile
//        fetchCaretakerProfile()
//    }
//
//    private fun logSessionData() {
//        try {
//            val user = SessionManager.getCurrentUser()
//            val token = SessionManager.getToken()
//            Log.d("SessionData", "User: $user")
//            Log.d("SessionData", "Token: $token")
//
//            // Optional: display session data as fallback
// //            txtName.setText(user.name ?: "")
// //            txtAddress.setText(user.address ?: "")
// //            txtDoB.setText(user.dob ?: "")
// //            txtPhone.setText(user.phone ?: "")
// //            txtUnderCare.setText(user.ward ?: "")
// //            txtMedicareNumber.setText(user.medicareNumber ?: "")
// //            txtEmergencyContact.setText(user.EmergencyContact ?: "")
//
//        } catch (e: Exception) {
//            Log.e("SessionData", "Failed to get session data: ${e.message}")
//        }
//    }
//
//    private fun fetchCaretakerProfile() {
//        val currentUser = try {
//            SessionManager.getCurrentUser()
//        } catch (e: Exception) {
//            txtLoading.visibility = View.GONE
//            Toast.makeText(this, "User not logged in", Toast.LENGTH_LONG).show()
//            return
//        }
//
//        val token = try {
//            "Bearer ${SessionManager.getToken()}"
//        } catch (e: Exception) {
//            txtLoading.visibility = View.GONE
//            Toast.makeText(this, "Token not found. Please login again.", Toast.LENGTH_LONG).show()
//            return
//        }
//
//        val caretakerIdentifier = currentUser.id ?: currentUser.email
//        if (caretakerIdentifier == null) {
//            txtLoading.visibility = View.GONE
//            Toast.makeText(this, "No ID or email found for user", Toast.LENGTH_LONG).show()
//            return
//        }
//
//        lifecycleScope.launch {
//            try {
//                val response = ApiClient.apiService.getCaretakerProfile(
//                    token = token,
//                    caretakerId = caretakerIdentifier
//                )
//
//                txtLoading.visibility = View.GONE
//
//                Log.d("CaretakerProfile", "Response code: ${response.code()}")
//                Log.d("CaretakerProfile", "Response body: ${response.body()}")
//
//                if (response.isSuccessful && response.body() != null) {
//                    caretaker = response.body()!!
//                    bindProfile(caretaker)
//                } else {
//                    Log.w("CaretakerProfile", "API returned null or empty, showing session data")
//                    Toast.makeText(
//                        this@CaretakerProfileActivity,
//                        "API data not available, using session data",
//                        Toast.LENGTH_LONG
//                    ).show()
//                }
//
//            } catch (e: HttpException) {
//                txtLoading.visibility = View.GONE
//                Log.e("CaretakerProfile", "HTTP exception: ${e.message()}")
//            } catch (e: Exception) {
//                txtLoading.visibility = View.GONE
//                Log.e("CaretakerProfile", "Exception: ${e.message}")
//            }
//        }
//    }
//
//    private fun bindProfile(c: Caretaker) {
//        txtName.setText(c.fullName ?: "")
//        txtAddress.setText("") // Not provided by API
//        txtDoB.setText("")     // Not provided by API
//        txtPhone.setText("")   // Not provided by API
//        txtUnderCare.setText(c.assignedPatients?.size?.toString() ?: "0")
//        txtMedicareNumber.setText("")
//        txtEmergencyContact.setText(c.email ?: "")
//
//        txtName.isEnabled = false
//        txtAddress.isEnabled = false
//        txtDoB.isEnabled = false
//        txtPhone.isEnabled = false
//        txtUnderCare.isEnabled = false
//        txtMedicareNumber.isEnabled = false
//        txtEmergencyContact.isEnabled = false
//    }
// }

package deakin.gopher.guardian.view.caretaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.Caretaker
import deakin.gopher.guardian.model.login.SessionManager
import deakin.gopher.guardian.services.api.ApiClient
import kotlinx.coroutines.launch
import retrofit2.HttpException

class CaretakerProfileActivity : AppCompatActivity() {
    private lateinit var txtName: TextInputEditText
    private lateinit var txtAddress: TextInputEditText
    private lateinit var txtDoB: TextInputEditText
    private lateinit var txtPhone: TextInputEditText
    private lateinit var txtUnderCare: TextInputEditText
    private lateinit var txtMedicareNumber: TextInputEditText
    private lateinit var txtEmail: TextInputEditText
    private lateinit var backBtn: MaterialButton
    private lateinit var editButton: ImageView
    private lateinit var txtLoading: TextView

    private lateinit var caretaker: Caretaker

    private val editActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val updatedCaretaker =
                    result.data?.getSerializableExtra("updatedCaretaker") as? Caretaker
                updatedCaretaker?.let {
                    caretaker = it
                    bindProfile(caretaker)
                    Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_caretakerprofile)

        // Initialize views
        txtName = findViewById(R.id.txtName)
        txtAddress = findViewById(R.id.txtAddress)
        txtDoB = findViewById(R.id.txtDoB)
        txtPhone = findViewById(R.id.txtPhone)
        txtUnderCare = findViewById(R.id.txtUnderCare)
        txtMedicareNumber = findViewById(R.id.txtMedicareNumber)
        txtEmail = findViewById(R.id.txtEmail)
        backBtn = findViewById(R.id.backBtn)
        editButton = findViewById(R.id.editButton)
        txtLoading = findViewById(R.id.txtLoading)

        backBtn.setOnClickListener { finish() }

        // Disable edit button until profile loads
        editButton.isEnabled = false

        editButton.setOnClickListener {
            val intent = Intent(this, EditCaretakerProfileActivity::class.java)
            intent.putExtra("caretaker", caretaker)
            editActivityLauncher.launch(intent)
        }

        fetchCaretakerProfile()
    }

    private fun fetchCaretakerProfile() {
        txtLoading.visibility = View.VISIBLE

        val token =
            try {
                "Bearer ${SessionManager.getToken()}"
            } catch (e: Exception) {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_LONG).show()
                return
            }

        val currentUser =
            try {
                SessionManager.getCurrentUser()
            } catch (e: Exception) {
                Toast.makeText(this, "User not found", Toast.LENGTH_LONG).show()
                return
            }

        val caretakerId = currentUser.id ?: ""

        lifecycleScope.launch {
            try {
                val response =
                    ApiClient.apiService.getCaretakerProfile(
                        token = token,
                        caretakerId = caretakerId,
                    )

                txtLoading.visibility = View.GONE

                if (response.isSuccessful && response.body() != null) {
                    caretaker = response.body()!!
                    bindProfile(caretaker)
                    editButton.isEnabled = true
                } else {
                    Toast.makeText(
                        this@CaretakerProfileActivity,
                        "Failed to fetch profile: ${response.code()}",
                        Toast.LENGTH_LONG,
                    ).show()
                }
            } catch (e: HttpException) {
                txtLoading.visibility = View.GONE
                Toast.makeText(
                    this@CaretakerProfileActivity,
                    "HTTP error: ${e.message()}",
                    Toast.LENGTH_LONG,
                ).show()
            } catch (e: Exception) {
                txtLoading.visibility = View.GONE
                Toast.makeText(
                    this@CaretakerProfileActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_LONG,
                ).show()
            }
        }
    }

    private fun bindProfile(c: Caretaker) {
        txtName.setText(c.fullName ?: "")
        txtEmail.setText(c.email ?: "")
        txtUnderCare.setText(c.assignedPatients?.size?.toString() ?: "0")

        // Read-only fields
        txtName.isEnabled = false
        txtAddress.isEnabled = false
        txtDoB.isEnabled = false
        txtPhone.isEnabled = false
        txtUnderCare.isEnabled = false
        txtMedicareNumber.isEnabled = false
        txtEmail.isEnabled = false
    }
}
