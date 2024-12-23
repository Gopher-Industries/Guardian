package deakin.gopher.guardian.view.general

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.login.SessionManager
import deakin.gopher.guardian.services.EmailPasswordAuthService
import deakin.gopher.guardian.view.FallDetection.FallAlertActivity
import deakin.gopher.guardian.view.caretaker.notifications.confirmincident.ConfirmIncidentActivity

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val getStartedButton = findViewById<Button>(R.id.getStartedButton)

           getStartedButton.setOnClickListener { _ -> onGetStartedClick() }

            FirebaseMessaging.getInstance()
                 .token
                 .addOnCompleteListener { task: Task<String?> ->
                     if (task.isSuccessful) {
                         val token = task.result
                         Log.d("FCM Token", token ?: "Token is null")
                     } else {
                         Log.w("MainActivity", "Fetching FCM registration token failed", task.exception)
                     }
                 }


        getStartedButton.setOnClickListener{
            startActivity(Intent(this@MainActivity, Homepage4caretaker::class.java))
        }
    }

    private fun onGetStartedClick() {
        if (!SessionManager.isLoggedIn) {
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        } else {
            startActivity(Intent(this@MainActivity, Homepage4caretaker::class.java))
        }
    }

    private fun onLogoutClick() {
        EmailPasswordAuthService.signOut(this)
        finish()
    }
}