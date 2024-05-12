package deakin.gopher.guardian.services

import android.content.Context
import android.content.Intent
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import deakin.gopher.guardian.model.login.EmailAddress
import deakin.gopher.guardian.model.login.Password
import android.util.Log  // Import statement for Log
import deakin.gopher.guardian.model.login.SessionManager
import deakin.gopher.guardian.view.general.LoginActivity
import com.google.firebase.auth.FirebaseUser
import java.util.Date
import java.util.concurrent.TimeUnit
import com.google.android.gms.tasks.Tasks

class EmailPasswordAuthService(
    private val emailAddress: EmailAddress,
    private val password: Password,
) {
    private val auth = FirebaseAuth.getInstance()
    private val userDataService = UserDataService()
    private val logTag = "EmailPwdAuthSvc"  // Shortened log tag


    fun signIn(): Task<AuthResult> {
        return try {
            // Attempt to sign in with Firebase Auth
            val signInTask = auth.signInWithEmailAndPassword(emailAddress.emailAddress, password.password)

            // We add a listener, but remember the original task is still returned
            signInTask.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result?.user?.let { user ->
                        checkPasswordExpiry(user.uid, user)
                    }
                } else {
                    Log.e("EmailPasswordAuthServ", "Sign-in failed: ${task.exception?.message}")
                }
            }
            signInTask  // This returns the original Task<AuthResult>
        } catch (e: Exception) {
            e.printStackTrace()
            // When an exception is caught, return a Task indicating failure
            Tasks.forException<AuthResult>(e)
        }
    }

    fun isUserVerified(): Boolean {
        return auth.currentUser?.isEmailVerified ?: false
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

    fun checkPasswordExpiry(userId: String, user: FirebaseUser) {
        userDataService.getLastPasswordChangeTimestamp(userId) { lastPasswordChangeTimestamp ->
            if (lastPasswordChangeTimestamp != null) {
                val lastChangeDate = Date(lastPasswordChangeTimestamp)
                val currentDate = Date()
                val diff = currentDate.time - lastChangeDate.time
                val daysPassed = TimeUnit.MILLISECONDS.toDays(diff)
                if (daysPassed > 90) {
                    // Call the existing resetPassword method
                    if (user.email != null) {
                        resetPassword(EmailAddress(user.email!!))
                    }
                } else {
                    Log.v("Auth", "Password is valid. Proceeding with login.")
                }
            } else {
                // Assume password needs resetting if no timestamp is found
                if (user.email != null) {
                    resetPassword(EmailAddress(user.email!!))
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

        fun signOut(context: Context) {
            try {
                SessionManager(context).logoutUser()
                FirebaseAuth.getInstance().signOut()
                context.startActivity(Intent(context, LoginActivity::class.java))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
