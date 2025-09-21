package deakin.gopher.guardian.view.general

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import deakin.gopher.guardian.model.login.SessionManager
import deakin.gopher.guardian.model.login.SessionManager.isLoggedIn
import deakin.gopher.guardian.model.login.SessionManager.logoutUser
import deakin.gopher.guardian.model.login.SessionManager.updateLastActiveTime

abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // 🔑 Apply night mode before super.onCreate() + setContentView()
        val sharedPreferences: SharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
        val isNightMode: Boolean = sharedPreferences.getBoolean("night_mode", false)

        if (isNightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()

        // ✅ Session timeout logic
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
