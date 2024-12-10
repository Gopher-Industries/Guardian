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
    private const val KEY_USER_ID: String = "KEY_USER_ID" // New key for userId
    private const val KEY_LAST_ACTIVE_TIME: String = "KEY_LAST_ACTIVE_TIME"

    private lateinit var pref: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    // Initialize the SharedPreferences and editor
    fun init(context: Context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        editor = pref.edit()
    }

    // Create login session
    fun createLoginSession(user: User, token: String) {
        setCurrentUser(user)
        setUserId(user.id)  // Set the userId
        setObject(KEY_TOKEN, token)
        editor.putBoolean(IS_LOGIN, true)
        editor.commit()
    }

    // Get stored token
    fun getToken(): String {
        return getObject(KEY_TOKEN, String::class.java)
            ?: throw UserNotAuthenticatedException("Token not found")
    }

    // Get stored user object
    fun getCurrentUser(): User {
        return getObject(KEY_CURRENT_USER, User::class.java)
            ?: throw UserNotAuthenticatedException("User not found")
    }

    // Get stored userId
    // Add this function in your SessionManager.kt
    fun getUserId(): String? {
        return try {
            getCurrentUser().id  // Assuming your User object has an 'id' property
        } catch (e: Exception) {
            null  // Return null if user is not found or there is an error
        }
    }

    // Set the current user in SharedPreferences
    private fun setCurrentUser(user: User) {
        setObject(KEY_CURRENT_USER, user)
    }

    // Set the userId in SharedPreferences
    private fun setUserId(userId: String) {
        editor.putString(KEY_USER_ID, userId).apply()
    }

    // Delete the current user
    fun deleteCurrentUser() {
        deleteObject(KEY_CURRENT_USER)
        deleteObject(KEY_USER_ID) // Also delete the userId
    }

    // Check if the user is logged in
    val isLoggedIn: Boolean
        get() = pref.getBoolean(IS_LOGIN, false)

    // Logout the user
    fun logoutUser() {
        editor.clear()
        editor.commit()
    }

    // Update the last active time
    fun updateLastActiveTime() {
        editor.putLong(KEY_LAST_ACTIVE_TIME, System.currentTimeMillis())
        editor.commit()
    }

    // Get the last active time
    fun getLastActiveTime(): Long {
        return pref.getLong(KEY_LAST_ACTIVE_TIME, 0)
    }

    // Save an object to SharedPreferences
    private fun setObject(key: String, `object`: Any?) {
        val objectJson = if (`object` != null) Gson().toJson(`object`) else null
        editor.putString(key, objectJson).apply()
    }

    // Get an object from SharedPreferences
    private fun <T> getObject(key: String, objectType: Type): T? {
        val objectJson = pref.getString(key, null)
        return if (objectJson != null) {
            Gson().fromJson(objectJson, objectType)
        } else {
            null
        }
    }

    // Delete an object from SharedPreferences
    private fun deleteObject(key: String) {
        editor.remove(key).apply()
    }
}


