package deakin.gopher.guardian.view.general

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.login.EmailAddress
import deakin.gopher.guardian.model.login.Password
import deakin.gopher.guardian.model.login.RoleName
import deakin.gopher.guardian.services.EmailPasswordAuthService
import deakin.gopher.guardian.services.NavigationService
import deakin.gopher.guardian.viewmodels.LoginViewModel

class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val mEmail: EditText = findViewById(R.id.Email)
        val mPassword: EditText = findViewById(R.id.password)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        val Auth = FirebaseAuth.getInstance()
        val loginButton: Button = findViewById(R.id.loginBtn)
        val mCreateBtn: Button = findViewById(R.id.createText)
        val forgotTextLink: TextView = findViewById(R.id.forgotPassword)

        loginButton.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            val emailInput = mEmail.text.toString().trim { it <= ' ' }
            val passwordInput = mPassword.text.toString().trim { it <= ' ' }

            val loginValidationError = validateInputs(emailInput, passwordInput)

            if (loginValidationError != null) {
                Toast.makeText(
                    applicationContext,
                    loginValidationError.message,
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            EmailPasswordAuthService(EmailAddress(emailInput), Password(passwordInput))
                .signIn()
                ?.addOnSuccessListener {
                    progressBar.visibility = View.VISIBLE
                    NavigationService(this).onLoginForRole(RoleName.Caretaker)
                }
                ?.addOnFailureListener { e: Exception ->
                    Toast.makeText(
                        applicationContext,
                        "Error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }

        mCreateBtn.setOnClickListener {
            NavigationService(this).onRegister()
        }

        forgotTextLink.setOnClickListener { v: View ->
            val resetMail = EditText(v.context)
            val passwordResetDialog = AlertDialog.Builder(v.context)
            passwordResetDialog.setTitle("Reset Password")
            passwordResetDialog.setMessage("Enter Your Email To Received Reset Link.")
            passwordResetDialog.setView(resetMail)
            passwordResetDialog.setPositiveButton(
                "Yes"
            ) { dialog: DialogInterface?, which: Int ->
                val mail = resetMail.text.toString()

                EmailPasswordAuthService.resetPassword(EmailAddress(mail))
                    ?.addOnSuccessListener {
                        Toast.makeText(
                            this@LoginActivity,
                            "Reset Link Sent To Your Email.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    ?.addOnFailureListener { e: Exception ->
                        Toast.makeText(
                            this@LoginActivity,
                            "Error. Reset Link is Not Sent. Reason: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
            passwordResetDialog.setNegativeButton(
                "No"
            ) { dialog: DialogInterface?, which: Int -> }
            passwordResetDialog.create().show()
        }
    }

    private fun validateInputs(
        rawEmail: String?,
        rawPassword: String?
    ): LoginValidationError? {
        if (rawEmail.isNullOrEmpty()) {
            return LoginValidationError.EmptyEmail
        }

        val emailAddress = EmailAddress(rawEmail)
        if (emailAddress.isValid().not()) {
            return LoginValidationError.InvalidEmail
        }

        if (rawPassword.isNullOrEmpty()) {
            return LoginValidationError.EmptyPassword
        }

        val password = Password(rawPassword)
        if (password.isValid().not()) {
            return LoginValidationError.PasswordTooShort
        }

        return null
    }
}

sealed class LoginValidationError(val message: String) {
    data object EmptyEmail : LoginValidationError("Email is Required.")
    data object InvalidEmail : LoginValidationError("Invalid Email Address")
    data object EmptyPassword : LoginValidationError("Password is Required.")
    data object PasswordTooShort : LoginValidationError("Password Must Be Longer than 6 Characters")
    data object EmptyRole : LoginValidationError("Please select a role")
}