package deakin.gopher.guardian.services

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import deakin.gopher.guardian.model.login.EmailAddress
import deakin.gopher.guardian.model.login.Password

class EmailPasswordAuthService(
    private val emailAddress: EmailAddress,
    private val password: Password
) {
    private val auth = FirebaseAuth.getInstance()
    fun signIn(): Task<AuthResult>? {
        return try {
            auth.signInWithEmailAndPassword(emailAddress.emailAddress, password.password)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

