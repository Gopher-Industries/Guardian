package deakin.gopher.guardian.model.login

import android.content.Context
import android.content.SharedPreferences

class SessionManager(private val _context: Context) {
    private val pref: SharedPreferences = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = pref.edit()
    private val lastActiveTime = "LastActiveTime"

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
        editor.putLong(lastActiveTime, System.currentTimeMillis())
        editor.commit()
    }

    fun getLastActiveTime(): Long {
        return pref.getLong(lastActiveTime, 0)
    }

    companion object {
        private const val PREF_NAME = "LoginPref"
        private const val IS_LOGIN = "IsLoggedIn"
        private const val TOKEN_SAVED = "isTokenSaved"
    }

    fun setToken(token: String?) {
        editor.putString("token", token)
        editor.putBoolean(TOKEN_SAVED, true)
        editor.apply()
    }

    fun getToken(): String? {
        return pref.getString("token", "")
    }
}
