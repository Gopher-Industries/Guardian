package deakin.gopher.guardian.view.general

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import deakin.gopher.guardian.model.login.SessionManager
import deakin.gopher.guardian.model.login.SessionManager.isLoggedIn
import deakin.gopher.guardian.model.login.SessionManager.logoutUser
import deakin.gopher.guardian.model.login.SessionManager.updateLastActiveTime

abstract class BaseActivity : AppCompatActivity() {
    override fun onResume() {
        super.onResume()

        // Check for user session expiry and timeout
        if (isLoggedIn) {
            val lastActiveTime: Long = SessionManager.getLastActiveTime()
            val currentTime = System.currentTimeMillis()
            val expiryTime = (30 * 60 * 1000).toLong()

            if (expiryTime < currentTime - lastActiveTime) {
                logoutUser()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                updateLastActiveTime()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        updateLastActiveTime()
    }
}
