package deakin.gopher.guardian.view.general

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import deakin.gopher.guardian.R
import deakin.gopher.guardian.TrainingActivity
import deakin.gopher.guardian.services.EmailPasswordAuthService
import deakin.gopher.guardian.view.FallDetection.FallDetectionActivity
import deakin.gopher.guardian.view.caretaker.CaretakerProfileActivity

class Homepage4caretaker : BaseActivity() {
    private lateinit var patientListButton: Button
    private lateinit var settingsButton: Button
    private lateinit var signOutButton: Button
    private lateinit var profileButton: Button
    private lateinit var taskListButton: Button
    private lateinit var trainingButton: Button
    private lateinit var monitorButton: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage4caretaker)
        patientListButton = findViewById(R.id.patientListButton)
        settingsButton = findViewById(R.id.settingsButton3)
        signOutButton = findViewById(R.id.sighOutButton)
        profileButton = findViewById(R.id.caretaker_profile)
        taskListButton = findViewById(R.id.taskListButton)
        trainingButton = findViewById(R.id.trainingButton)
        monitorButton = findViewById(R.id.monitorButton)

        // patient list button
        patientListButton.setOnClickListener {
            val medicalDiagnosticsActivityIntent =
                Intent(this@Homepage4caretaker, PatientListActivity::class.java)
            medicalDiagnosticsActivityIntent.putExtra("userType", "caretaker")
            startActivity(medicalDiagnosticsActivityIntent)
        }

        // settings button
        settingsButton.setOnClickListener {
            val medicalDiagnosticsActivityIntent =
                Intent(this@Homepage4caretaker, Setting::class.java)
            medicalDiagnosticsActivityIntent.putExtra("userType", "caretaker")
            startActivity(medicalDiagnosticsActivityIntent)
        }

        // tasklist button
        taskListButton.setOnClickListener {
            startActivity(
                Intent(this@Homepage4caretaker, TasksListActivity::class.java),
            )
        }

        // sign out button
        signOutButton.setOnClickListener {
            EmailPasswordAuthService.signOut(this)
            finish()
        }

        profileButton.setOnClickListener {
            val medicalDiagnosticsActivityIntent =
                Intent(this@Homepage4caretaker, CaretakerProfileActivity::class.java)
            startActivity(medicalDiagnosticsActivityIntent)
        }

        monitorButton.setOnClickListener {
            val fallDetectionActivityIntent =
                Intent(this@Homepage4caretaker, FallDetectionActivity::class.java)
            startActivity(fallDetectionActivityIntent)
        }

        // training button
        trainingButton.setOnClickListener {
            startActivity(
                Intent(this@Homepage4caretaker, TrainingActivity::class.java),
            )
        }
    }
}
