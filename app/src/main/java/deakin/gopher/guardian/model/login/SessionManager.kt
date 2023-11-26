package deakin.gopher.guardian.model.login

import android.content.Context
import android.content.SharedPreferences

class SessionManager(private val _context: Context) {
    private val pref: SharedPreferences = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    private val editor: SharedPreferences.Editor = pref.edit();
    private val LAST_ACTIVE_TIME = "LastActiveTime";

    fun createLoginSession() {
        editor.putBoolean(IS_LOGIN, true)
        editor.commit()
    }

    val isLoggedIn: Boolean
        get() = pref.getBoolean(IS_LOGIN, false)

    fun logoutUser() {
        editor.clear()
        editor.commit()
    }

    fun updateLastActiveTime() {
        editor.putLong(LAST_ACTIVE_TIME, System.currentTimeMillis())
        editor.commit()
    }

    fun getLastActiveTime(): Long {
        return pref.getLong(LAST_ACTIVE_TIME, 0)
    }

    companion object {
        private const val PREF_NAME = "LoginPref"
        private const val IS_LOGIN = "IsLoggedIn"
    }
}
