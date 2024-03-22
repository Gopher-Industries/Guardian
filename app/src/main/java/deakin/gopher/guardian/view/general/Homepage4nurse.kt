package deakin.gopher.guardian.view.general

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import deakin.gopher.guardian.R
import deakin.gopher.guardian.services.EmailPasswordAuthService
import deakin.gopher.guardian.services.NavigationService

class Homepage4nurse : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage4nurse)
        val tasksButton: Button = findViewById(R.id.tasksButton_nurse)
        val settingsButton: Button = findViewById(R.id.settingsButton_nurse)
        val signOutButton: Button = findViewById(R.id.sighOutButton_nurse)

        tasksButton.setOnClickListener {
            NavigationService(this).onLaunchTasks()
        }

        // settings button
        settingsButton.setOnClickListener {
            NavigationService(this).onSettings()
        }

        // sign out button
        signOutButton.setOnClickListener {
            EmailPasswordAuthService.signOut(this)
            finish()
        }
    }
}
