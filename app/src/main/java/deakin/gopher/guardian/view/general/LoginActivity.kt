package deakin.gopher.guardian.view.general

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.gson.Gson
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.ApiErrorResponse
import deakin.gopher.guardian.model.BaseModel
import deakin.gopher.guardian.model.login.EmailAddress
import deakin.gopher.guardian.model.login.LoginValidationError
import deakin.gopher.guardian.model.login.Role
import deakin.gopher.guardian.model.login.SessionManager
import deakin.gopher.guardian.model.register.AuthResponse
import deakin.gopher.guardian.services.NavigationService
import deakin.gopher.guardian.services.api.ApiClient
import deakin.gopher.guardian.view.hide
import deakin.gopher.guardian.view.show
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : BaseActivity() {
    private lateinit var gsoClient: GoogleSignInClient

    // Facebook
    private lateinit var callbackManager: CallbackManager
    private lateinit var fbLoginButton: LoginButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // --- Initialize Facebook SDK ---
        FacebookSdk.setApplicationId(getString(R.string.facebook_app_id))
        FacebookSdk.setClientToken(getString(R.string.facebook_client_token))
        @Suppress("DEPRECATION")
        FacebookSdk.sdkInitialize(applicationContext)

        val mEmail: EditText = findViewById(R.id.Email)
        val mPassword: EditText = findViewById(R.id.password)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        val loginButton: Button = findViewById(R.id.loginBtn)
        val loginGoogleButton: SignInButton = findViewById(R.id.loginGoogleBtn)
        val mCreateBtn: Button = findViewById(R.id.loginRegisterBtn)
        val forgotTextLink: TextView = findViewById(R.id.forgotPassword)

        // --- Facebook setup ---
        callbackManager = CallbackManager.Factory.create()
        fbLoginButton = findViewById(R.id.loginFacebookBtn)
        fbLoginButton.setPermissions("email", "public_profile")
        fbLoginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                val credential = FacebookAuthProvider.getCredential(result.accessToken.token)
                FirebaseAuth.getInstance().signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            NavigationService(this@LoginActivity).toHomeScreenForRole(Role.Caretaker)
                        } else {
                            showMessage(getString(R.string.toast_login_error, task.exception?.message))
                        }
                    }
            }
            override fun onCancel() {
                showMessage("Facebook login cancelled")
            }
            override fun onError(error: FacebookException) {
                showMessage("Facebook login error: ${error.message}")
            }
        })

        // --- Google setup (as you had) ---
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            // If Firebase Google sign-in ever fails with null idToken, add:
            // .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        gsoClient = GoogleSignIn.getClient(this, gso)

        loginButton.setOnClickListener {
            progressBar.show()
            val emailInput = mEmail.text.toString().trim { it <= ' ' }
            val passwordInput = mPassword.text.toString().trim { it <= ' ' }

            val loginValidationError = validateInputs(emailInput)
            if (loginValidationError != null) {
                progressBar.hide()
                Toast.makeText(
                    applicationContext,
                    loginValidationError.messageResoureId,
                    Toast.LENGTH_LONG,
                ).show()
                return@setOnClickListener
            }

            val call = ApiClient.apiService.login(emailInput, passwordInput)
            call.enqueue(object : Callback<AuthResponse> {
                override fun onResponse(
                    call: Call<AuthResponse>,
                    response: Response<AuthResponse>,
                ) {
                    progressBar.hide()
                    if (response.isSuccessful && response.body() != null) {
                        val user = response.body()!!.user
                        val token = response.body()!!.token
                        SessionManager.createLoginSession(user, token)
                        NavigationService(this@LoginActivity).toPinCodeActivity(user.role)
                    } else {
                        val errorResponse = Gson().fromJson(
                            response.errorBody()?.string(),
                            ApiErrorResponse::class.java,
                        )
                        showMessage(errorResponse.apiError ?: response.message())
                    }
                }
                override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                    progressBar.hide()
                    showMessage(getString(R.string.toast_login_error, t.message))
                }
            })
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
            passwordResetDialog.setPositiveButton(getString(R.string.yes)) { _: DialogInterface?, _: Int ->
                val mail = resetMail.text.toString()
                sendResetPasswordEmail(mail)
            }
            passwordResetDialog.setNegativeButton(getString(R.string.no)) { _: DialogInterface?, _: Int -> }
            passwordResetDialog.create().show()
        }
    }

    private fun validateInputs(rawEmail: String?): LoginValidationError? {
        if (rawEmail.isNullOrEmpty()) return LoginValidationError.EmptyEmail
        val emailAddress = EmailAddress(rawEmail)
        if (!emailAddress.isValid()) return LoginValidationError.InvalidEmail
        return null
    }

    private fun sendResetPasswordEmail(userEmail: String) {
        val call = ApiClient.apiService.requestPasswordReset(userEmail)
        call.enqueue(object : Callback<BaseModel> {
            override fun onResponse(call: Call<BaseModel>, response: Response<BaseModel>) {
                if (response.isSuccessful && response.body() != null) {
                    showMessage(
                        response.body()!!.apiMessage
                            ?: getString(R.string.toast_reset_link_sent_to_your_email),
                    )
                } else {
                    val errorResponse = Gson().fromJson(
                        response.errorBody()?.string(),
                        ApiErrorResponse::class.java,
                    )
                    showMessage(errorResponse.apiError ?: response.message())
                }
            }
            override fun onFailure(call: Call<BaseModel>, t: Throwable) {
                showMessage("Error sending password reset link: ${t.message}")
            }
        })
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Facebook first
        if (::callbackManager.isInitialized) {
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }

        // Google
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
                    NavigationService(this).toHomeScreenForRole(Role.Caretaker)
                } else {
                    showMessage(getString(R.string.toast_login_error, authTask.exception?.message))
                }
            }
        } catch (e: ApiException) {
            showMessage(getString(R.string.toast_login_error, e.message))
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val RC_GOOGLE_SIGN_IN = 1000
    }
}


