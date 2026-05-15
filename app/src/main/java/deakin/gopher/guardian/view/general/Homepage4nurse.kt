package deakin.gopher.guardian.view.general

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.login.SessionManager
import deakin.gopher.guardian.services.EmailPasswordAuthService
import deakin.gopher.guardian.services.NavigationService

import android.content.Intent


class Homepage4nurse : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage4nurse)

        val user = SessionManager.getCurrentUser()

        val titleText: TextView =
            findViewById(R.id.medicalDiagnosticsTitleTextView)

        titleText.append(" ${user.name.split(" ").getOrNull(0) ?: ""}")

        val patientsButton: Button =
            findViewById(R.id.patientsButton_nurse)

        val settingsButton: Button =
            findViewById(R.id.settingsButton_nurse)

        val signOutButton: Button =
            findViewById(R.id.sighOutButton_nurse)

        val profileButton: Button =
            findViewById(R.id.profileButton_nurse)

        // My Patients button
        patientsButton.setOnClickListener {
            NavigationService(this).onLaunchPatientList()
        }

        // Settings button
        settingsButton.setOnClickListener {
            NavigationService(this).onSettings()
        }

        // My Profile button
        profileButton.setOnClickListener {
            val intent = Intent(this, NurseProfileActivity::class.java)
            startActivity(intent)
        }


        // Sign Out button
        signOutButton.setOnClickListener {
            EmailPasswordAuthService.signOut(this)
            finish()
        }
    }
}