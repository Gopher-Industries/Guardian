//package deakin.gopher.guardian.view.doctor
//
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import android.view.View
//import android.widget.ImageView
//import android.widget.TextView
//import android.widget.Toast
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.appcompat.app.AppCompatActivity
//import androidx.lifecycle.lifecycleScope
//import com.google.android.material.button.MaterialButton
//import com.google.android.material.textfield.TextInputEditText
//import deakin.gopher.guardian.R
//import deakin.gopher.guardian.model.Doctor
//import deakin.gopher.guardian.model.login.SessionManager
//import deakin.gopher.guardian.services.api.ApiClient
//import kotlinx.coroutines.launch
//import retrofit2.HttpException
//import kotlin.jvm.java
//
//class DoctorProfileActivity : AppCompatActivity() {
//
//    // UI components
//    private lateinit var txtName: TextInputEditText
//    private lateinit var txtSpecialization: TextInputEditText
//    private lateinit var txtPhone: TextInputEditText
//    private lateinit var txtEmail: TextInputEditText
//    private lateinit var txtAssignedPatients: TextInputEditText
//    private lateinit var txtHospital: TextInputEditText
//    private lateinit var backBtn: MaterialButton
//    private lateinit var editButton: ImageView
//    private lateinit var txtLoading: TextView
//
//    private lateinit var doctor: Doctor
//
//    private val editActivityLauncher =
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            if (result.resultCode == RESULT_OK) {
//                val updatedDoctor =
//                    result.data?.getSerializableExtra("updatedDoctor") as? Doctor
//                updatedDoctor?.let {
//                    doctor = it
//                    bindProfile(doctor)
//                    Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_doctor_profile)
//
//        // Initialize views
//        txtName = findViewById(R.id.txtName)
//        txtSpecialization = findViewById(R.id.txtSpecialization)
//        txtPhone = findViewById(R.id.txtPhone)
//        txtEmail = findViewById(R.id.txtEmail)
//        txtAssignedPatients = findViewById(R.id.txtAssignedPatients)
//        txtHospital = findViewById(R.id.txtHospital)
//        backBtn = findViewById(R.id.backBtn)
//        editButton = findViewById(R.id.editButton)
//        txtLoading = findViewById(R.id.txtLoading)
//
//        backBtn.setOnClickListener { finish() }
//
//        // Disable edit button until profile loads
//        editButton.isEnabled = false
//
//        editButton.setOnClickListener {
//            val intent = Intent(this, EditDoctorProfileActivity::class.java)
//            intent.putExtra("doctor", doctor)
//            editActivityLauncher.launch(intent)
//        }
//
//        fetchDoctorProfile()
//    }
//
//    private fun fetchDoctorProfile() {
//        txtLoading.visibility = View.VISIBLE
//
//        val token = try {
//            "Bearer ${SessionManager.getToken()}"
//        } catch (e: Exception) {
//            txtLoading.visibility = View.GONE
//            Toast.makeText(this, "User not logged in", Toast.LENGTH_LONG).show()
//            return
//        }
//
//        val currentUser = try {
//            SessionManager.getCurrentUser()
//        } catch (e: Exception) {
//            txtLoading.visibility = View.GONE
//            Toast.makeText(this, "User not found", Toast.LENGTH_LONG).show()
//            return
//        }
//
//        val doctorId = currentUser.id ?: ""
//
//        lifecycleScope.launch {
//            try {
//                val response = ApiClient.apiService.getDoctorProfile(
//                    token = token,
//                    doctorId = doctorId
//                )
//
//                txtLoading.visibility = View.GONE
//
//                if (response.isSuccessful && response.body() != null) {
//                    doctor = response.body()!!
//                    bindProfile(doctor)
//                    editButton.isEnabled = true
//                } else {
//                    Toast.makeText(
//                        this@DoctorProfileActivity,
//                        "Failed to fetch profile: ${response.code()}",
//                        Toast.LENGTH_LONG
//                    ).show()
//                }
//
//            } catch (e: HttpException) {
//                txtLoading.visibility = View.GONE
//                Log.e("DoctorProfile", "HTTP error: ${e.message()}")
//                Toast.makeText(
//                    this@DoctorProfileActivity,
//                    "HTTP error: ${e.message()}",
//                    Toast.LENGTH_LONG
//                ).show()
//            } catch (e: Exception) {
//                txtLoading.visibility = View.GONE
//                Log.e("DoctorProfile", "Error: ${e.message}")
//                Toast.makeText(
//                    this@DoctorProfileActivity,
//                    "Error: ${e.message}",
//                    Toast.LENGTH_LONG
//                ).show()
//            }
//        }
//    }
//
//    private fun bindProfile(d: Doctor) {
//        txtName.setText(d.fullName ?: "")
//        txtSpecialization.setText(d.specialization ?: "")
//        txtPhone.setText(d.phone ?: "")
//        txtEmail.setText(d.email ?: "")
//        txtAssignedPatients.setText(d.assignedPatients?.size?.toString() ?: "0")
//        txtHospital.setText(d.hospital ?: "")
//
//        // Make all fields read-only
//        txtName.isEnabled = false
//        txtSpecialization.isEnabled = false
//        txtPhone.isEnabled = false
//        txtEmail.isEnabled = false
//        txtAssignedPatients.isEnabled = false
//        txtHospital.isEnabled = false
//    }
//}

package deakin.gopher.guardian.view.doctor

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.Doctor

class DoctorProfileActivity : AppCompatActivity() {

    private lateinit var txtName: TextInputEditText
    private lateinit var txtSpecialization: TextInputEditText
    private lateinit var txtPhone: TextInputEditText
    private lateinit var txtEmail: TextInputEditText
    private lateinit var txtHospital: TextInputEditText
    private lateinit var editButton: ImageView
    private lateinit var backButton: MaterialButton

    private lateinit var doctor: Doctor

    private val editActivityLauncher =
        registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val updatedDoctor = result.data?.getSerializableExtra("updatedDoctor") as? Doctor
                updatedDoctor?.let {
                    doctor = it
                    bindProfile(doctor)
                    Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_profile)

        // Initialize views
        txtName = findViewById(R.id.txtName)
        txtSpecialization = findViewById(R.id.txtSpecialization)
        txtPhone = findViewById(R.id.txtPhone)
        txtEmail = findViewById(R.id.txtEmail)
        txtHospital = findViewById(R.id.txtHospital)
        editButton = findViewById(R.id.editButton)


        // Create a dummy doctor object (static)
        doctor = Doctor(
            fullName = "Dr. John Doe",
            specialization = "Cardiology",
            phone = "123-456-7890",
            email = "john.doe@example.com",
            hospital = "City Hospital"
        )

        // Bind profile
        bindProfile(doctor)

        // Disable edit button until we need it
        editButton.isEnabled = true

        // Edit button launches EditDoctorProfileActivity
        editButton.setOnClickListener {
            val intent = Intent(this, EditDoctorProfileActivity::class.java)
            intent.putExtra("doctor", doctor)
            editActivityLauncher.launch(intent)
        }

        // Back button
        backButton.setOnClickListener {
            finish()
        }
    }

    private fun bindProfile(d: Doctor) {
        txtName.setText(d.fullName ?: "")
        txtSpecialization.setText(d.specialization ?: "")
        txtPhone.setText(d.phone ?: "")
        txtEmail.setText(d.email ?: "")
        txtHospital.setText(d.hospital ?: "")

        // Make fields read-only
        txtName.isEnabled = false
        txtSpecialization.isEnabled = false
        txtPhone.isEnabled = false
        txtEmail.isEnabled = false
        txtHospital.isEnabled = false
    }
}

