package deakin.gopher.guardian

import android.app.Application
import deakin.gopher.guardian.model.login.SessionManager

class GuardianApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SessionManager.init(this)
    }
}
