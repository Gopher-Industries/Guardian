package deakin.gopher.guardian.view.general

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.ApiErrorResponse
import deakin.gopher.guardian.model.BaseModel
import deakin.gopher.guardian.model.login.LoginValidationError
import deakin.gopher.guardian.model.login.RoleName
import deakin.gopher.guardian.model.login.SessionManager
import deakin.gopher.guardian.model.register.AuthResponse
import deakin.gopher.guardian.services.NavigationService
import deakin.gopher.guardian.services.api.ApiClient
import deakin.gopher.guardian.view.hide
import deakin.gopher.guardian.view.show
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.gson.Gson

class LoginActivity : BaseActivity() {
    private var userRole: RoleName = RoleName.Caretaker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Skip login for testing purposes
        startActivity(Intent(this, AdminDashboardActivity::class.java)) // Replace with your target activity
        finish()

        // Comment out or keep login-related logic for future use
        setContentView(R.layout.activity_login)

        val mEmail: EditText = findViewById(R.id.Email)
        val mPassword: EditText = findViewById(R.id.password)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        val loginButton: Button = findViewById(R.id.loginBtn)
        val mCreateBtn: Button = findViewById(R.id.loginRegisterBtn)
        val forgotTextLink: TextView = findViewById(R.id.forgotPassword)
        val loginRoleRadioGroup: RadioGroup = findViewById(R.id.login_role_radioGroup)

        loginRoleRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            userRole = when (checkedId) {
                R.id.admin_radioButton -> RoleName.Admin
                R.id.caretaker_radioButton -> RoleName.Caretaker
                R.id.nurse_radioButton -> RoleName.Nurse
                else -> RoleName.Caretaker
            }
        }

        loginButton.setOnClickListener {
            val emailInput = mEmail.text.toString().trim()
            val passwordInput = mPassword.text.toString().trim()

            val validationError = validateInputs(emailInput)
            if (validationError != null) {
                Toast.makeText(applicationContext, validationError.messageResoureId, Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            progressBar.show()
            performLogin(emailInput, passwordInput, progressBar)
        }

        mCreateBtn.setOnClickListener {
            NavigationService(this).toRegistration()
        }

        forgotTextLink.setOnClickListener { v ->
            showPasswordResetDialog(v)
        }
    }

    private fun validateInputs(rawEmail: String?): LoginValidationError? {
        if (rawEmail.isNullOrEmpty()) {
            return LoginValidationError.EmptyEmail
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(rawEmail).matches()) {
            return LoginValidationError.InvalidEmail
        }

        return null
    }

    private fun performLogin(email: String, password: String, progressBar: ProgressBar) {
        val call = ApiClient.apiService.login(email, password)

        call.enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                progressBar.hide()
                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    SessionManager.createLoginSession(authResponse.user, authResponse.token)
                    NavigationService(this@LoginActivity).toHomeScreenForRole(userRole)
                } else {
                    val errorResponse = Gson().fromJson(response.errorBody()?.string(), ApiErrorResponse::class.java)
                    Toast.makeText(this@LoginActivity, errorResponse.apiError ?: response.message(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                progressBar.hide()
                Toast.makeText(this@LoginActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showPasswordResetDialog(view: View) {
        val resetMail = EditText(view.context)
        val passwordResetDialog = AlertDialog.Builder(view.context)
            .setTitle(getString(R.string.text_reset_password))
            .setMessage(getString(R.string.text_enter_your_email_to_received_reset_link))
            .setView(resetMail)
            .setPositiveButton(getString(R.string.yes)) { _: DialogInterface, _: Int ->
                val email = resetMail.text.toString().trim()
                sendResetPasswordEmail(email)
            }
            .setNegativeButton(getString(R.string.no), null)
            .create()

        passwordResetDialog.show()
    }

    private fun sendResetPasswordEmail(email: String) {
        val call = ApiClient.apiService.requestPasswordReset(email)
        call.enqueue(object : Callback<BaseModel> {
            override fun onResponse(call: Call<BaseModel>, response: Response<BaseModel>) {
                if (response.isSuccessful && response.body() != null) {
                    Toast.makeText(this@LoginActivity, response.body()!!.apiMessage ?: getString(R.string.toast_reset_link_sent_to_your_email), Toast.LENGTH_SHORT).show()
                } else {
                    val errorResponse = Gson().fromJson(response.errorBody()?.string(), ApiErrorResponse::class.java)
                    Toast.makeText(this@LoginActivity, errorResponse.apiError ?: response.message(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<BaseModel>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
