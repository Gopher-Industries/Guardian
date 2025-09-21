package deakin.gopher.guardian.view.general

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import deakin.gopher.guardian.R
import deakin.gopher.guardian.services.EmailPasswordAuthService
import deakin.gopher.guardian.view.falldetection.FallDetectionActivity
import deakin.gopher.guardian.view.prescription.PrescriptionActivity
import deakin.gopher.guardian.view.doctor.DoctorProfileActivity

class Homepage4doctor : BaseActivity() {
    private lateinit var patientListButton: Button
    private lateinit var settingsButton: Button
    private lateinit var signOutButton: Button
    private lateinit var profileButton: Button
    private lateinit var prescriptionButton: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage4doctor)

        // Button initializations
        patientListButton = findViewById(R.id.patientListButton)
        settingsButton = findViewById(R.id.settingsButton3)
        signOutButton = findViewById(R.id.signOutButton)
        profileButton = findViewById(R.id.doctor_profile)
        prescriptionButton = findViewById(R.id.prescriptionButton)

        // Patient list button
        patientListButton.setOnClickListener {
            val medicalDiagnosticsActivityIntent =
                Intent(this@Homepage4doctor, PatientListActivity::class.java)
            medicalDiagnosticsActivityIntent.putExtra("userType", "doctor")
            startActivity(medicalDiagnosticsActivityIntent)
        }

        // Settings button
        settingsButton.setOnClickListener {
            val medicalDiagnosticsActivityIntent =
                Intent(this@Homepage4doctor, Setting::class.java)
            medicalDiagnosticsActivityIntent.putExtra("userType", "doctor")
            startActivity(medicalDiagnosticsActivityIntent)
        }

        // Sign out button
        signOutButton.setOnClickListener {
            EmailPasswordAuthService.signOut(this)
            finish()
        }

        // Profile button
        profileButton.setOnClickListener {
            val medicalDiagnosticsActivityIntent =
                Intent(this@Homepage4doctor, DoctorProfileActivity::class.java)
            startActivity(medicalDiagnosticsActivityIntent)
        }

        // Prescription button
        prescriptionButton.setOnClickListener {
            val prescriptionIntent =
                Intent(this@Homepage4doctor, PrescriptionActivity::class.java)
            startActivity(prescriptionIntent)
        }
    }

    @OptIn(UnstableApi::class)
    fun startFallDetectionActivity() {
        val fallDetectionActivityIntent =
            Intent(this@Homepage4doctor, FallDetectionActivity::class.java)
        startActivity(fallDetectionActivityIntent)
    }
}
