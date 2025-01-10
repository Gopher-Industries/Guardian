package deakin.gopher.guardian.view.general

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.BaseModel
import deakin.gopher.guardian.model.login.RoleName
import deakin.gopher.guardian.model.login.SessionManager
import deakin.gopher.guardian.model.register.AuthResponse
import deakin.gopher.guardian.services.NavigationService
import deakin.gopher.guardian.services.api.ApiClient
import deakin.gopher.guardian.view.show
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

        loginRoleRadioGroup.setOnCheckedChangeListener { _, checkedId ->
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
            val email = mEmail.text.toString().trim()
            val password = mPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                progressBar.visibility = View.VISIBLE

                ApiClient.apiService.login(email, password).enqueue(
                    object : Callback<BaseModel<AuthResponse>> {
                        override fun onResponse(
                            call: Call<BaseModel<AuthResponse>>,
                            response: Response<BaseModel<AuthResponse>>,
                        ) {
                            progressBar.visibility = View.GONE
                            if (response.isSuccessful) {
                                response.body()?.data?.let { authResponse ->
                                    // Save session details
                                    SessionManager.createLoginSession(authResponse.user, authResponse.token)

                                    // Navigate to the appropriate home page
                                    val intent =
                                        when (authResponse.user.roleName) {
                                            "admin" -> Intent(this@LoginActivity, Homepage4admin::class.java)
                                            "caretaker" -> Intent(this@LoginActivity, Homepage4caretaker::class.java)
                                            "nurse" -> Intent(this@LoginActivity, Homepage4nurse::class.java)
                                            else -> null
                                        }

                                    if (intent != null) {
                                        startActivity(intent)
                                        finish() // End LoginActivity
                                    } else {
                                        Toast.makeText(
                                            this@LoginActivity,
                                            "Unknown role. Cannot navigate.",
                                            Toast.LENGTH_SHORT,
                                        ).show()
                                    }
                                } ?: run {
                                    Toast.makeText(this@LoginActivity, "Unexpected response format", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(this@LoginActivity, "Login failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(
                            call: Call<BaseModel<AuthResponse>>,
                            t: Throwable,
                        ) {
                            progressBar.visibility = View.GONE
                            Toast.makeText(this@LoginActivity, "Login failed: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    },
                )
            } else {
                Toast.makeText(this, "Please enter both email and password.", Toast.LENGTH_SHORT).show()
            }
        }

        forgotTextLink.setOnClickListener {
            val resetMail = EditText(this)
            AlertDialog.Builder(this)
                .setTitle("Reset Password")
                .setMessage("Enter your email to receive reset instructions")
                .setView(resetMail)
                .setPositiveButton("Send") { _, _ ->
                    sendResetPasswordEmail(resetMail.text.toString())
                }
                .setNegativeButton("Cancel", null)
                .create()
                .show()
        }

        mCreateBtn.setOnClickListener {
            NavigationService(this).toRegistration()
        }
    }

    private fun sendResetPasswordEmail(email: String) {
        ApiClient.apiService.requestPasswordReset(email).enqueue(
            object : Callback<BaseModel<Unit>> {
                override fun onResponse(
                    call: Call<BaseModel<Unit>>,
                    response: Response<BaseModel<Unit>>,
                ) {
                    if (response.isSuccessful) {
                        showMessage("Reset link sent to your email.")
                    } else {
                        showMessage("Failed to send reset link.")
                    }
                }

                override fun onFailure(
                    call: Call<BaseModel<Unit>>,
                    t: Throwable,
                ) {
                    showMessage("Error: ${t.message}")
                }
            },
        )
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
