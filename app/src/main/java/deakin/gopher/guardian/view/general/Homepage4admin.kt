package deakin.gopher.guardian.view.general

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import deakin.gopher.guardian.R
import deakin.gopher.guardian.services.EmailPasswordAuthService
import deakin.gopher.guardian.view.patient.dailyreport.DailyReportActivity

class Homepage4admin : BaseActivity() {
    private lateinit var newPatientButton: Button
    private lateinit var dailyReportButton: Button
    private lateinit var patientListButton: Button
    private lateinit var settingsButton: Button
    private lateinit var signOutButton: Button
    private lateinit var nurseRosterButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage4admin)

        newPatientButton = findViewById(R.id.newPaitentButton)
        dailyReportButton = findViewById(R.id.dailyReportButton4caretaker)
        patientListButton = findViewById(R.id.patientListButton)
        settingsButton = findViewById(R.id.settingsButton)
        signOutButton = findViewById(R.id.sighOutButton)
        nurseRosterButton = findViewById(R.id.nurseRoseterButton)

        onBackPressedDispatcher.addCallback(this) {
            AlertDialog.Builder(this@Homepage4admin)
                .setTitle(R.string.sign_out)
                .setMessage(R.string.sign_out_confirmation_message)
                .setPositiveButton(R.string.sign_out) { _, _ ->
                    EmailPasswordAuthService.signOut(this@Homepage4admin)
                    finish()
                }
                .setNegativeButton(R.string.stay_in, null)
                .show()
        }

        newPatientButton.setOnClickListener {
            val medicalDiagnosticsActivityIntent =
                Intent(this@Homepage4admin, PatientProfileAddActivity::class.java)
            startActivity(medicalDiagnosticsActivityIntent)
        }

        dailyReportButton.setOnClickListener {
            val medicalDiagnosticsActivityIntent =
                Intent(this@Homepage4admin, DailyReportActivity::class.java)
            startActivity(medicalDiagnosticsActivityIntent)
        }

        patientListButton.setOnClickListener {
            val medicalDiagnosticsActivityIntent =
                Intent(this@Homepage4admin, PatientListActivity::class.java)
            medicalDiagnosticsActivityIntent.putExtra("userType", "admin")
            startActivity(medicalDiagnosticsActivityIntent)
        }

        settingsButton.setOnClickListener {
            val medicalDiagnosticsActivityIntent =
                Intent(this@Homepage4admin, Setting::class.java)
            medicalDiagnosticsActivityIntent.putExtra("userType", "admin")
            startActivity(medicalDiagnosticsActivityIntent)
        }

        signOutButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle(R.string.sign_out)
                .setMessage(R.string.sign_out_confirmation_message)
                .setPositiveButton(R.string.sign_out) { _, _ ->
                    EmailPasswordAuthService.signOut(this)
                    finish()
                }
                .setNegativeButton(R.string.stay_in, null)
                .show()
        }

        nurseRosterButton.setOnClickListener {
            val nurseRosterActivityIntent =
                Intent(this@Homepage4admin, NurseRosterActivity::class.java)
            startActivity(nurseRosterActivityIntent)
        }
    }
}
