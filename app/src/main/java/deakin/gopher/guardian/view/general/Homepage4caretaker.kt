package deakin.gopher.guardian.view.general

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import deakin.gopher.guardian.R
import deakin.gopher.guardian.services.EmailPasswordAuthService

class Homepage4caretaker : BaseActivity() {
    private lateinit var patientListButton: Button
    private lateinit var settingsButton: Button
    private lateinit var signOutButton: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage4caretaker)
        patientListButton = findViewById(R.id.patientListButton)
        settingsButton = findViewById(R.id.settingsButton3)
        signOutButton = findViewById(R.id.sighOutButton)

        // patient list button
        patientListButton.setOnClickListener {
            val medicalDiagnosticsActivityIntent =
                Intent(this@Homepage4caretaker, PatientListActivity::class.java)
            startActivity(medicalDiagnosticsActivityIntent)
        }

        // settings button
        settingsButton.setOnClickListener {
            val medicalDiagnosticsActivityIntent =
                Intent(this@Homepage4caretaker, Setting::class.java)
            startActivity(medicalDiagnosticsActivityIntent)
        }

        // sign out button
        signOutButton.setOnClickListener {
            EmailPasswordAuthService.signOut(this)
            finish()
        }
    }
}
