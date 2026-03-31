package deakin.gopher.guardian.view.general

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.login.SessionManager

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Using activity_main as splash for now as it has the logo

        Handler(Looper.getMainLooper()).postDelayed({
            checkSessionAndNavigate()
        }, 1500)
    }

    private fun checkSessionAndNavigate() {
        if (SessionManager.isLoggedIn) {
            try {
                val user = SessionManager.getCurrentUser()
                // If logged in, go to Home Screen for their role
                val intent = when (user.role) {
                    deakin.gopher.guardian.model.login.Role.Admin -> Intent(this, Homepage4admin::class.java)
                    deakin.gopher.guardian.model.login.Role.Caretaker -> Intent(this, Homepage4caretaker::class.java)
                    deakin.gopher.guardian.model.login.Role.Nurse -> Intent(this, Homepage4nurse::class.java)
                }
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            } catch (e: Exception) {
                // If user data is corrupted or not found, go to Login
                startActivity(Intent(this, LoginActivity::class.java))
            }
        } else {
            // Not logged in, go to Login screen
            startActivity(Intent(this, LoginActivity::class.java))
        }
        finish()
    }
}
