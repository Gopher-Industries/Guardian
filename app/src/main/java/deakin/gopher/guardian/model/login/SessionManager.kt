package deakin.gopher.guardian.model.login

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.UserNotAuthenticatedException
import com.google.gson.Gson
import deakin.gopher.guardian.model.register.User
import java.lang.reflect.Type

object SessionManager {
    private const val PREF_NAME = "LoginPref"
    private const val IS_LOGIN = "IsLoggedIn"
    private const val KEY_CURRENT_USER: String = "KEY_CURRENT_USER"
    private const val KEY_TOKEN: String = "KEY_TOKEN"
    private const val KEY_LAST_ACTIVE_TIME: String = "KEY_LAST_ACTIVE_TIME"

    private lateinit var pref: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    fun init(context: Context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        editor = pref.edit()
    }

    fun createLoginSession(
        user: User,
        token: String,
    ) {
        setCurrentUser(user)
        setObject(KEY_TOKEN, token)
        editor.putBoolean(IS_LOGIN, true)
        editor.commit()
    }

    fun getToken(): String {
        return getObject(KEY_TOKEN, String::class.java)
            ?: throw UserNotAuthenticatedException("Token not found")
    }

    fun getCurrentUser(): User {
        return getObject(KEY_CURRENT_USER, User::class.java)
            ?: throw UserNotAuthenticatedException("User not found")
    }

    private fun setCurrentUser(user: User) {
        setObject(KEY_CURRENT_USER, user)
    }

    fun deleteCurrentUser() {
        deleteObject(KEY_CURRENT_USER)
    }

    val isLoggedIn: Boolean
        get() = pref.getBoolean(IS_LOGIN, false)

    fun logoutUser() {
        editor.clear()
        editor.commit()
    }

    fun updateLastActiveTime() {
        editor.putLong(KEY_LAST_ACTIVE_TIME, System.currentTimeMillis())
        editor.commit()
    }

    fun getLastActiveTime(): Long {
        return pref.getLong(KEY_LAST_ACTIVE_TIME, 0)
    }

    private fun setObject(
        key: String,
        `object`: Any?,
    ) {
        val objectJson = if (`object` != null) Gson().toJson(`object`) else null
        editor.putString(key, objectJson).apply()
    }

    private fun <T> getObject(
        key: String,
        objectType: Type,
    ): T? {
        val objectJson = pref.getString(key, null)
        return if (objectJson != null) {
            Gson().fromJson(objectJson, objectType)
        } else {
            null
        }
    }

    private fun deleteObject(key: String) {
        editor.remove(key).apply()
    }
}
