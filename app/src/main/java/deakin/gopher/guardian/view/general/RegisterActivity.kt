package deakin.gopher.guardian.view.general

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.RegistrationStatusMessage
import deakin.gopher.guardian.model.login.EmailAddress
import deakin.gopher.guardian.model.login.Password
import deakin.gopher.guardian.model.register.RegistrationError
import deakin.gopher.guardian.services.EmailPasswordAuthService
import deakin.gopher.guardian.services.NavigationService

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        val mEmail: EditText = findViewById(R.id.Email)
        val mPassword: EditText = findViewById(R.id.password)
        val passwordConfirmation: EditText = findViewById(R.id.passwordConfirm)
        val backToLoginButton: Button = findViewById(R.id.backToLoginButton)
        val mRegisterBtn: Button = findViewById(R.id.registerBtn)
        val mLoginBtn: Button = findViewById(R.id.loginRegisterBtn)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)

        mRegisterBtn.setOnClickListener {
            val emailInput = mEmail.text.toString().trim { it <= ' ' }
            val passwordInput = mPassword.text.toString().trim { it <= ' ' }
            val passwordConfirmInput = passwordConfirmation.text.toString().trim { it <= ' ' }

            val registrationError = validateInputs(emailInput, passwordInput, passwordConfirmInput)

            if (registrationError != null) {
                Toast.makeText(
                    applicationContext,
                    registrationError.messageResourceId,
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            progressBar.visibility = View.VISIBLE

            val emailAddress = EmailAddress(emailInput)
            val password = Password(passwordInput)

            EmailPasswordAuthService(emailAddress, password)
                .createAccount()
                ?.addOnSuccessListener {
                    Toast.makeText(
                        this@RegisterActivity,
                        RegistrationStatusMessage.Success.message,
                        Toast.LENGTH_SHORT
                    ).show()

                    NavigationService(this).toLogin()
                }
                ?.addOnFailureListener { e: Exception ->
                    Toast.makeText(
                        this@RegisterActivity,
                        RegistrationStatusMessage.Failure.toString() + " : ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }

        mLoginBtn.setOnClickListener {
            NavigationService(this).toLogin()
        }

        backToLoginButton.setOnClickListener {
            NavigationService(this).toLogin()
        }
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