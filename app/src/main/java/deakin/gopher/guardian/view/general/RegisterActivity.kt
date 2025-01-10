package deakin.gopher.guardian.view.general

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.isVisible
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.BaseModel
import deakin.gopher.guardian.model.login.EmailAddress
import deakin.gopher.guardian.model.login.Password
import deakin.gopher.guardian.model.register.AuthResponse
import deakin.gopher.guardian.model.register.RegisterRequest
import deakin.gopher.guardian.model.register.RegistrationError
import deakin.gopher.guardian.services.NavigationService
import deakin.gopher.guardian.services.api.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.account_creation)

        val mFullName: EditText = findViewById(R.id.Fullname)
        val mEmail: EditText = findViewById(R.id.Email)
        val mPassword: EditText = findViewById(R.id.password)
        val passwordConfirmation: EditText = findViewById(R.id.passwordConfirm)
        val roleButton: MaterialButtonToggleGroup = findViewById(R.id.role_toggle_group)
        val backToLoginButton: Button = findViewById(R.id.backToLoginButton)
        val mRegisterBtn: Button = findViewById(R.id.registerBtn)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)

        mRegisterBtn.setOnClickListener {
            val emailInput = mEmail.text.toString().trim()
            val passwordInput = mPassword.text.toString().trim()
            val passwordConfirmInput = passwordConfirmation.text.toString().trim()
            val nameInput = mFullName.text.toString().trim()
            val roleInput = roleButton.checkedButtonId

            val registrationError =
                validateInputs(
                    emailInput,
                    passwordInput,
                    passwordConfirmInput,
                    nameInput,
                    roleInput,
                )

            if (registrationError != null) {
                Toast.makeText(
                    applicationContext,
                    getString(registrationError.messageResourceId),
                    Toast.LENGTH_LONG,
                ).show()
                return@setOnClickListener
            }

            progressBar.visibility = View.VISIBLE

            val emailAddress = EmailAddress(emailInput)
            val password = Password(passwordInput)
            val role = findViewById<MaterialButton>(roleInput).text.toString().lowercase()

            val request =
                RegisterRequest(
                    email = emailAddress.emailAddress,
                    password = password.password,
                    name = nameInput,
                    role = role,
                )

            val call = ApiClient.apiService.register(request)
            call.enqueue(
                object : Callback<BaseModel<AuthResponse>> {
                    override fun onResponse(
                        call: Call<BaseModel<AuthResponse>>,
                        response: Response<BaseModel<AuthResponse>>,
                    ) {
                        progressBar.isVisible = false
                        if (response.isSuccessful) {
                            response.body()?.data?.let {
                                showMessage("Registration successful")
                                NavigationService(this@RegisterActivity).toLogin()
                            } ?: showMessage("Registration failed: Missing user data")
                        } else {
                            showMessage("Registration failed: ${response.message()}")
                        }
                    }

                    override fun onFailure(
                        call: Call<BaseModel<AuthResponse>>,
                        t: Throwable,
                    ) {
                        progressBar.isVisible = false
                        showMessage("Error: ${t.localizedMessage}")
                    }
                },
            )
        }

        backToLoginButton.setOnClickListener {
            finish()
        }
    }

    private fun validateInputs(
        rawEmail: String?,
        rawPassword: String?,
        rawConfirmedPassword: String?,
        rawName: String?,
        roleInput: Int,
    ): RegistrationError? {
        if (rawEmail.isNullOrEmpty()) return RegistrationError.EmptyEmail
        val emailAddress = EmailAddress(rawEmail)
        if (!emailAddress.isValid()) return RegistrationError.InvalidEmail
        if (rawPassword.isNullOrEmpty()) return RegistrationError.EmptyPassword
        val password = Password(rawPassword)
        if (!password.isValid()) return RegistrationError.PasswordTooShort
        if (rawConfirmedPassword.isNullOrEmpty()) return RegistrationError.EmptyConfirmedPassword
        if (!password.confirmWith(rawConfirmedPassword)) return RegistrationError.PasswordsFailConfirmation
        if (rawName.isNullOrEmpty()) return RegistrationError.EmptyName
        if (roleInput == View.NO_ID) return RegistrationError.EmptyRole
        return null
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
