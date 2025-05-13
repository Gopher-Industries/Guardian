package deakin.gopher.guardian.view.general

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import deakin.gopher.guardian.model.login.SessionManager
import deakin.gopher.guardian.model.login.SessionManager.isLoggedIn
import deakin.gopher.guardian.model.login.SessionManager.logoutUser
import deakin.gopher.guardian.model.login.SessionManager.updateLastActiveTime
import deakin.gopher.guardian.view.caretaker.CaretakerProfileActivity

abstract class BaseActivity : AppCompatActivity() {

    /**
     * Subclasses can override this to launch a specific profile activity.
     */
    open fun openProfileActivity() {
        // Default implementation, override if needed
        val intent = Intent(this, CaretakerProfileActivity::class.java)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()

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
