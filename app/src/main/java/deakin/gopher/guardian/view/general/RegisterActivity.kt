package deakin.gopher.guardian.view.general

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.login.EmailAddress
import deakin.gopher.guardian.model.login.Password
import deakin.gopher.guardian.model.register.RegistrationError

class RegisterActivity : AppCompatActivity() {
    var userID: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        val mEmail: EditText = findViewById(R.id.Email)
        val mPassword: EditText = findViewById(R.id.password)
        val passwordConfirmation: EditText = findViewById(R.id.passwordConfirm)
        val backToLoginButton: Button = findViewById(R.id.backToLoginButton)
        val mRegisterBtn: Button = findViewById(R.id.registerBtn)
        val mLoginBtn: Button = findViewById(R.id.loginRegisterBtn)


        val Auth = FirebaseAuth.getInstance()
        val fStore = FirebaseFirestore.getInstance()

        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        mRegisterBtn.setOnClickListener { v: View? ->
            val emailInput = mEmail.text.toString().trim { it <= ' ' }
            val password = mPassword.text.toString().trim { it <= ' ' }
            val passwordConfirm = passwordConfirmation.text.toString().trim { it <= ' ' }

            val registrationError = validateInputs(emailInput, password, passwordConfirm)

            if (registrationError != null) {
                Toast.makeText(
                    this@RegisterActivity,
                    registrationError.message,
                    Toast.LENGTH_SHORT
                )
                    .show()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(emailInput)) {
                mEmail.error = "Email is Required."
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(password)) {
                mPassword.error = "Password is Required."
                return@setOnClickListener
            }
            if (6 > password.length) {
                mPassword.error = "Password Must be >= 6 Characters"
                return@setOnClickListener
            }
            progressBar.visibility = View.VISIBLE

            // register the user in firebase
            Auth.createUserWithEmailAndPassword(emailInput, password)
                .addOnCompleteListener { task: Task<AuthResult?> ->
                    if (task.isSuccessful) {

                        // send verification link
                        val fuser = Auth.currentUser
                        if (fuser != null) {
                            fuser
                                .sendEmailVerification()
                                .addOnSuccessListener { aVoid: Void? ->
                                    Toast.makeText(
                                        this@RegisterActivity,
                                        "Verification Email Has been Sent.",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                                .addOnFailureListener { e: Exception ->
                                    Log.d(
                                        TAG,
                                        "onFailure: Email not sent " + e.message
                                    )
                                }
                        }
                        Toast.makeText(
                            this@RegisterActivity,
                            "User Created.",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        userID = Auth.currentUser!!.uid
                        val documentReference = fStore.collection("users").document(
                            userID!!
                        )
                        val user: MutableMap<String, Any> = HashMap()
                        user["email"] = emailInput
                        documentReference
                            .set(user)
                            .addOnSuccessListener { aVoid: Void? ->
                                Log.d(
                                    TAG,
                                    "onSuccess: user Profile is created for $userID"
                                )
                            }
                            .addOnFailureListener { e: Exception ->
                                Log.d(
                                    TAG,
                                    "onFailure: $e"
                                )
                            }
                        startActivity(Intent(applicationContext, MainActivity::class.java))
                    } else {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Error ! " + task.exception!!.message,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        progressBar.visibility = View.GONE
                    }
                }
        }

        mLoginBtn.setOnClickListener { v: View? ->
            startActivity(
                Intent(
                    applicationContext, LoginActivity::class.java
                )
            )
        }

        backToLoginButton.setOnClickListener { view: View? ->
            val loginIntent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }

    companion object {
        const val TAG = "TAG"
    }

    private fun validateInputs(
        rawEmail: String?,
        rawPassword: String?,
        rawConfirmedPassword: String?
    ): RegistrationError? {
        if (rawEmail.isNullOrEmpty()) {
            return RegistrationError.EmptyEmail
        }

        val emailAddress = EmailAddress(rawEmail)
        if (emailAddress.isValid().not()) {
            return RegistrationError.InvalidEmail
        }

        if (rawPassword.isNullOrEmpty()) {
            return RegistrationError.EmptyPassword
        }

        val password = Password(rawPassword)
        if (password.isValid().not()) {
            return RegistrationError.PasswordTooShort
        }

        if (rawConfirmedPassword.isNullOrEmpty()) {
            return RegistrationError.EmptyConfirmedPassword
        }

        if (password.confirmWith(rawConfirmedPassword).not()) {
            return RegistrationError.PasswordsFailConfirmation
        }

        return null
    }
}