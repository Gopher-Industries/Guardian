package deakin.gopher.guardian.view.general

import androidx.appcompat.app.AppCompatActivity
abstract class BaseActivity : AppCompatActivity() {
    protected open fun onResume() {
        super.onResume()

        // Check for user session expiry and timeout
        val sessionManager = deakin.gopher.guardian.model.login.SessionManager(this)
        if (sessionManager.isLoggedIn) {
            val lastActiveTime = sessionManager.getLastActiveTime()
            val currentTime = System.currentTimeMillis()
            val expiryTime = (30 * 60 * 1000).toLong()
            if (expiryTime < currentTime - lastActiveTime) {
                sessionManager.logoutUser()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                sessionManager.updateLastActiveTime()
            }
        }
    }

    protected open fun onPause() {
        super.onPause()
        deakin.gopher.guardian.model.login.SessionManager(this).updateLastActiveTime()
    }
}