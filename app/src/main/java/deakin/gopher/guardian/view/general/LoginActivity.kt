package deakin.gopher.guardian.view.general

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.login.EmailAddress
import deakin.gopher.guardian.model.login.LoginAuthError
import deakin.gopher.guardian.model.login.LoginValidationError
import deakin.gopher.guardian.model.login.Password
import deakin.gopher.guardian.model.login.RoleName
import deakin.gopher.guardian.model.login.SessionManager
import deakin.gopher.guardian.services.EmailPasswordAuthService
import deakin.gopher.guardian.services.NavigationService
import deakin.gopher.guardian.view.hide
import deakin.gopher.guardian.view.show

class LoginActivity : BaseActivity() {
    private var userRole: RoleName = RoleName.Caretaker
    private lateinit var gsoClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val mEmail: EditText = findViewById(R.id.Email)
        val mPassword: EditText = findViewById(R.id.password)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        val loginButton: Button = findViewById(R.id.loginBtn)
        val loginGoogleButton: SignInButton = findViewById(R.id.loginGoogleBtn)
        val mCreateBtn: Button = findViewById(R.id.loginRegisterBtn)
        val forgotTextLink: TextView = findViewById(R.id.forgotPassword)
        val loginRoleRadioGroup: RadioGroup = findViewById(R.id.login_role_radioGroup)

        loginRoleRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            val radioButton: RadioButton = findViewById(checkedId)

            // Set the user role based on the selected radio button
            userRole =
                when (checkedId) {
                    R.id.admin_radioButton -> RoleName.Admin
                    R.id.caretaker_radioButton -> RoleName.Caretaker
                    R.id.nurse_radioButton -> RoleName.Nurse
                    else -> RoleName.Caretaker
                }
        }

        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        gsoClient = GoogleSignIn.getClient(this, gso)

        loginButton.setOnClickListener {
            progressBar.show()
            val emailInput = mEmail.text.toString().trim { it <= ' ' }
            val passwordInput = mPassword.text.toString().trim { it <= ' ' }

            val loginValidationError = validateInputs(emailInput, passwordInput)

            if (loginValidationError != null) {
                progressBar.hide()
                Toast.makeText(
                    applicationContext,
                    loginValidationError.messageResoureId,
                    Toast.LENGTH_LONG,
                ).show()
                return@setOnClickListener
            }

            EmailPasswordAuthService(
                EmailAddress(emailInput),
                Password(passwordInput),
            ).also { authService ->
                authService
                    .signIn()
                    ?.addOnSuccessListener {
                        progressBar.show()

                        if (authService.isUserVerified().not()) {
                            progressBar.hide()
                            Toast.makeText(
                                applicationContext,
                                LoginAuthError.EmailNotVerified.messageId,
                                Toast.LENGTH_SHORT,
                            ).show()
                            return@addOnSuccessListener
                        }

                        NavigationService(this).toHomeScreenForRole(userRole)
                        progressBar.hide()
                    }
                    ?.addOnFailureListener { e: Exception ->
                        progressBar.hide()
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.toast_login_error, e.message),
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
            }
            val sessionManager = SessionManager(this)
            sessionManager.createLoginSession()
        }

        mCreateBtn.setOnClickListener {
            NavigationService(this).toRegistration()
        }

        loginGoogleButton.setOnClickListener {
            val signInIntent = gsoClient.signInIntent
            startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN)
        }

        forgotTextLink.setOnClickListener { v: View ->
            val resetMail = EditText(v.context)
            val passwordResetDialog = AlertDialog.Builder(v.context)

            with(passwordResetDialog) {
                setTitle(getString(R.string.text_reset_password))
                setMessage(getString(R.string.text_enter_your_email_to_received_reset_link))
                setView(resetMail)
            }

            passwordResetDialog.setPositiveButton(
                getString(R.string.yes),
            ) { _: DialogInterface?, _: Int ->
                val mail = resetMail.text.toString()

                EmailPasswordAuthService.resetPassword(EmailAddress(mail))
                    ?.addOnSuccessListener {
                        Toast.makeText(
                            this@LoginActivity,
                            getString(R.string.toast_reset_link_sent_to_your_email),
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                    ?.addOnFailureListener { e: Exception ->
                        Toast.makeText(
                            this@LoginActivity,
                            getString(
                                R.string.toast_error_reset_link_is_not_sent_reason,
                                e.message,
                            ),
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
            }
            passwordResetDialog.setNegativeButton(
                getString(R.string.no),
            ) { _: DialogInterface?, _: Int -> }
            passwordResetDialog.create().show()
        }
    }

    private fun validateInputs(
        rawEmail: String?,
        rawPassword: String?,
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

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            val auth = FirebaseAuth.getInstance()
            auth.signInWithCredential(credential).addOnCompleteListener { authTask ->
                if (authTask.isSuccessful) {
                    NavigationService(this).toHomeScreenForRole(userRole)
                } else {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.toast_login_error, authTask.exception?.message),
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
        } catch (e: ApiException) {
            Toast.makeText(
                applicationContext,
                getString(R.string.toast_login_error, e.message),
                Toast.LENGTH_SHORT,
            ).show()
        }
    }

    companion object {
        private const val RC_GOOGLE_SIGN_IN = 1000
    }
}
