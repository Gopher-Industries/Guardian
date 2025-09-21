package deakin.gopher.guardian.view.doctor

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.Doctor

class EditDoctorProfileActivity : AppCompatActivity() {
    private lateinit var txtName: TextInputEditText
    private lateinit var txtSpecialization: TextInputEditText
    private lateinit var txtPhone: TextInputEditText
    private lateinit var txtEmail: TextInputEditText
    private lateinit var txtHospital: TextInputEditText
    private lateinit var btnSave: MaterialButton
    private lateinit var btnCancel: MaterialButton

    private lateinit var doctor: Doctor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_doctor_profile)

        // Get doctor object from intent
        doctor = intent.getSerializableExtra("doctor") as? Doctor ?: Doctor()

        // Initialize views
        txtName = findViewById(R.id.txtName)
        txtSpecialization = findViewById(R.id.txtSpecialization)
        txtPhone = findViewById(R.id.txtPhone)
        txtEmail = findViewById(R.id.txtEmail)
        txtHospital = findViewById(R.id.txtHospital)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)

        // Populate fields
        txtName.setText(doctor.fullName ?: "")
        txtSpecialization.setText(doctor.specialization ?: "")
        txtPhone.setText(doctor.phone ?: "")
        txtEmail.setText(doctor.email ?: "")
        txtHospital.setText(doctor.hospital ?: "")

        // Save button
        btnSave.setOnClickListener {
            doctor.fullName = txtName.text.toString()
            doctor.specialization = txtSpecialization.text.toString()
            doctor.phone = txtPhone.text.toString()
            doctor.email = txtEmail.text.toString()
            doctor.hospital = txtHospital.text.toString()

            // Return updated doctor object to previous activity
            val resultIntent = intent
            resultIntent.putExtra("updatedDoctor", doctor)
            setResult(Activity.RESULT_OK, resultIntent)
            Toast.makeText(this, "Profile saved!", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Cancel button
        btnCancel.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }
}
