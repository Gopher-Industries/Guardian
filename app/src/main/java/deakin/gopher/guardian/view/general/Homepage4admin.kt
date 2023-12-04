package deakin.gopher.guardian.view.general

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import deakin.gopher.guardian.R
import deakin.gopher.guardian.services.EmailPasswordAuthService
import deakin.gopher.guardian.view.patient.dailyreport.DailyReportActivity

class Homepage4admin : BaseActivity() {
    private lateinit var newPatientButton: Button
    private lateinit var dailyReportButton: Button
    private lateinit var patientListButton: Button
    private lateinit var settingsButton: Button
    private lateinit var signOutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage4admin)
        newPatientButton = findViewById(R.id.newPaitentButton)
        dailyReportButton = findViewById(R.id.dailyReportButton4caretaker)
        patientListButton = findViewById(R.id.patientListButton)
        settingsButton = findViewById(R.id.settingsButton)
        signOutButton = findViewById(R.id.sighOutButton)

        // new Patient Button
        newPatientButton.setOnClickListener {
            val medicalDiagnosticsActivityIntent =
                Intent(this@Homepage4admin, PatientProfileAddActivity::class.java)
            startActivity(medicalDiagnosticsActivityIntent)
        }

        // daily report button
        dailyReportButton.setOnClickListener {
            val medicalDiagnosticsActivityIntent =
                Intent(this@Homepage4admin, DailyReportActivity::class.java)
            startActivity(medicalDiagnosticsActivityIntent)
        }
        // patient list button
        patientListButton.setOnClickListener {
            val medicalDiagnosticsActivityIntent =
                Intent(this@Homepage4admin, PatientListActivity::class.java)
            startActivity(medicalDiagnosticsActivityIntent)
        }

        // settings button
        settingsButton.setOnClickListener {
            val medicalDiagnosticsActivityIntent =
                Intent(this@Homepage4admin, Setting::class.java)
            startActivity(medicalDiagnosticsActivityIntent)
        }

        // sign out button
        signOutButton.setOnClickListener {
            EmailPasswordAuthService.signOut(this)
            finish()
        }
    }
}
