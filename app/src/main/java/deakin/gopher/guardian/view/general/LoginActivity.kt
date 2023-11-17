package deakin.gopher.guardian.view.general

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.login.EmailAddress
import deakin.gopher.guardian.model.login.LoginValidationError
import deakin.gopher.guardian.model.login.Password
import deakin.gopher.guardian.model.login.RoleName
import deakin.gopher.guardian.services.EmailPasswordAuthService
import deakin.gopher.guardian.services.NavigationService

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val mEmail: EditText = findViewById(R.id.Email)
        val mPassword: EditText = findViewById(R.id.password)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        val loginButton: Button = findViewById(R.id.loginBtn)
        val mCreateBtn: Button = findViewById(R.id.loginRegisterBtn)
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
                        getString(R.string.toast_login_error, e.message),
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
            ) { _: DialogInterface?, _: Int ->
                val mail = resetMail.text.toString()

                EmailPasswordAuthService.resetPassword(EmailAddress(mail))
                    ?.addOnSuccessListener {
                        Toast.makeText(
                            this@LoginActivity,
                            getString(R.string.toast_reset_link_sent_to_your_email),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    ?.addOnFailureListener { e: Exception ->
                        Toast.makeText(
                            this@LoginActivity,
                            getString(
                                R.string.toast_error_reset_link_is_not_sent_reason,
                                e.message
                            ),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
            passwordResetDialog.setNegativeButton(
                "No"
            ) { _: DialogInterface?, _: Int -> }
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

