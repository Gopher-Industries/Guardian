package deakin.gopher.guardian.services

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import deakin.gopher.guardian.model.login.EmailAddress
import deakin.gopher.guardian.model.login.Password
import android.util.Log  // Import statement for Log

class EmailPasswordAuthService(
    private val emailAddress: EmailAddress,
    private val password: Password,
) {
    private val auth = FirebaseAuth.getInstance()
    private val userDataService = UserDataService()
    private val logTag = "EmailPwdAuthSvc"  // Shortened log tag

    fun signIn(): Task<AuthResult>? {
        return try {
            auth.signInWithEmailAndPassword(emailAddress.emailAddress, password.password)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun createAccount(): Task<AuthResult>? {
        return try {
            val registerResult =
                auth.createUserWithEmailAndPassword(emailAddress.emailAddress, password.password)

            if (auth.currentUser == null) {
                registerResult
            } else {
                val currentUser = auth.currentUser!!
                currentUser.sendEmailVerification()
                userDataService.create(currentUser.uid, emailAddress.emailAddress)
                // Update the lastPasswordChange timestamp
                userDataService.updateLastPasswordChangeTimestamp(currentUser.uid,
                    System.currentTimeMillis())
                registerResult
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun updatePassword(newPassword: String): Task<Void>? {
        val currentUser = auth.currentUser
        return currentUser?.let { user ->
            user.updatePassword(newPassword).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Password update successful, update the timestamp
                    userDataService.updateLastPasswordChangeTimestamp(user.uid, System.currentTimeMillis())
                    Log.v(logTag, "Password updated successfully for User ${user.uid}")
                } else {
                    // Failed to update password
                    Log.e(logTag, "Failed to update password: ${task.exception}")
                }
            }
        }
    }

    companion object {
        fun resetPassword(emailAddress: EmailAddress): Task<Void>? {
            return try {
                FirebaseAuth.getInstance().sendPasswordResetEmail(emailAddress.emailAddress)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}
