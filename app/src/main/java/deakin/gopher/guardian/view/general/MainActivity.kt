package deakin.gopher.guardian.view.general

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.login.RoleName
import deakin.gopher.guardian.model.login.SessionManager
import deakin.gopher.guardian.services.EmailPasswordAuthService
import deakin.gopher.guardian.view.FallDetection.FallAlertActivity
import deakin.gopher.guardian.view.caretaker.notifications.confirmincident.ConfirmIncidentActivity

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val getStartedButton = findViewById<Button>(R.id.getStartedButton)


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
        getStartedButton.setOnClickListener { _ -> onGetStartedClick() }

    }



    private fun onGetStartedClick() {
        if (!SessionManager.isLoggedIn) {
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        } else {
            // Retrieve the user roleName from SessionManager
            val userRoleName = SessionManager.getCurrentUser().roleName // Use roleName instead of role

            when (userRoleName) {
                getString(R.string.company_admin_role_name) -> {
                    startActivity(Intent(this@MainActivity, Homepage4admin::class.java))
                }
                getString(R.string.caretaker_role_name) -> {
                    startActivity(Intent(this@MainActivity, Homepage4caretaker::class.java))
                }
                getString(R.string.nurse_role_name) -> {
                    startActivity(Intent(this@MainActivity, Homepage4nurse::class.java))
                }
                else -> {
                    Toast.makeText(this, "Unknown role: $userRoleName", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                }
            }
        }
    }


}
