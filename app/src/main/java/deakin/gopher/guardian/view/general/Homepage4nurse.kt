package deakin.gopher.guardian.view.general

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.login.SessionManager
import deakin.gopher.guardian.services.EmailPasswordAuthService
import deakin.gopher.guardian.services.NavigationService

class Homepage4nurse : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage4nurse)

        val user = SessionManager.getCurrentUser()

        val titleText: TextView =
            findViewById(R.id.medicalDiagnosticsTitleTextView)

        titleText.append(" ${user.name.split(" ").getOrNull(0) ?: ""}")

        // Existing buttons
        val patientsButton: Button =
            findViewById(R.id.patientsButton_nurse)

        val settingsButton: Button =
            findViewById(R.id.settingsButton_nurse)

        val signOutButton: Button =
            findViewById(R.id.sighOutButton_nurse)

        // New Patient Activities button
        val patientActivitiesButton: Button =
            findViewById(R.id.patientActivitiesButton_nurse)

        // Patients button
        patientsButton.setOnClickListener {
            NavigationService(this).onLaunchPatientList()
        }

        // Settings button
        settingsButton.setOnClickListener {
            NavigationService(this).onSettings()
        }

        // Patient Activities button
        patientActivitiesButton.setOnClickListener {

            // Temporary navigation
            // Replace PatientActivitiesActivity later if needed

            startActivity(
                Intent(
                    this,
                    PatientActivitiesActivity::class.java
                )
            )
        }

        // Sign out button
        signOutButton.setOnClickListener {
            EmailPasswordAuthService.signOut(this)
            finish()
        }
    }
}