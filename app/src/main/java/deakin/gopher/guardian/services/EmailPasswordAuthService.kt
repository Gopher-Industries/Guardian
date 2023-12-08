package deakin.gopher.guardian.services

import android.content.Context
import android.content.Intent
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import deakin.gopher.guardian.model.login.EmailAddress
import deakin.gopher.guardian.model.login.Password
import deakin.gopher.guardian.model.login.SessionManager
import deakin.gopher.guardian.view.general.LoginActivity

class EmailPasswordAuthService(
    private val emailAddress: EmailAddress,
    private val password: Password,
) {
    private val auth = FirebaseAuth.getInstance()
    private val userDataService = UserDataService()

    fun signIn(): Task<AuthResult>? {
        return try {
            auth.signInWithEmailAndPassword(emailAddress.emailAddress, password.password)
        } catch (e: Exception) {
            e.printStackTrace()
            null
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
                registerResult
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
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
