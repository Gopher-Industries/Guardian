package deakin.gopher.guardian

import android.app.Application
import deakin.gopher.guardian.model.login.SessionManager

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize SessionManager
        SessionManager.init(applicationContext)
    }
}
