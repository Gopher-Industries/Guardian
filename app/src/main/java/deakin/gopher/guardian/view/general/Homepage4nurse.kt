package deakin.gopher.guardian.view.general

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import deakin.gopher.guardian.R
import deakin.gopher.guardian.services.NavigationService

class Homepage4nurse : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage4nurse)
        val tasksButton: Button = findViewById(R.id.tasksButton_nurse)
        val settingsButton: Button = findViewById(R.id.settingsButton_nurse)
        val signOutButton: Button = findViewById(R.id.sighOutButton_nurse)

        // patient list button
        tasksButton.setOnClickListener(
            View.OnClickListener { view: View? ->
                val medicalDiagnosticsActivityIntent =
                    Intent(this@Homepage4nurse, PatientListActivity::class.java)
                startActivity(medicalDiagnosticsActivityIntent)
            })

        // settings button
        settingsButton.setOnClickListener {
            NavigationService(this).onSettings()
        }

        // sign out button
        signOutButton.setOnClickListener {
            NavigationService(this).onSignOut()
        }
    }
}