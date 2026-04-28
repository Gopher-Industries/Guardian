package deakin.gopher.guardian.view.general

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.ApiErrorResponse
import deakin.gopher.guardian.model.RegistrationStatusMessage
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
        val nameLayout: TextInputLayout = findViewById(R.id.nameTextInputLayout)
        val emailLayout: TextInputLayout = findViewById(R.id.emailTextInputLayout)
        val passwordLayout: TextInputLayout = findViewById(R.id.passwordTextInputLayout)
        val confirmPasswordLayout: TextInputLayout = findViewById(R.id.confirmPasswordTextInputLayout)
        val roleButton: MaterialButtonToggleGroup = findViewById(R.id.role_toggle_group)
        val backToLoginButton: Button = findViewById(R.id.backToLoginButton)
        val mRegisterBtn: Button = findViewById(R.id.registerBtn)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)

        mFullName.addTextChangedListener { nameLayout.error = null }
        mEmail.addTextChangedListener { emailLayout.error = null }
        mPassword.addTextChangedListener { passwordLayout.error = null }
        passwordConfirmation.addTextChangedListener { confirmPasswordLayout.error = null }
        roleButton.addOnButtonCheckedListener { _, _, _ -> 
            // Optional: clear role error if you add one to the layout
        }

        mRegisterBtn.setOnClickListener {
            val emailInput = mEmail.text.toString().trim { it <= ' ' }
            val passwordInput = mPassword.text.toString().trim { it <= ' ' }
            val passwordConfirmInput = passwordConfirmation.text.toString().trim { it <= ' ' }
            val nameInput = mFullName.text.toString().trim { it <= ' ' }
            val roleId = roleButton.checkedButtonId

            val registrationError =
                validateInputs(
                    emailInput,
                    passwordInput,
                    passwordConfirmInput,
                    nameInput,
                    roleId,
                )

            if (registrationError != null) {
                when (registrationError) {
                    RegistrationError.EmptyName -> nameLayout.error = getString(registrationError.messageResourceId)
                    RegistrationError.EmptyEmail, RegistrationError.InvalidEmail -> emailLayout.error = getString(registrationError.messageResourceId)
                    RegistrationError.EmptyPassword, RegistrationError.PasswordTooShort -> passwordLayout.error = getString(registrationError.messageResourceId)
                    RegistrationError.EmptyConfirmedPassword, RegistrationError.PasswordsFailConfirmation -> confirmPasswordLayout.error = getString(registrationError.messageResourceId)
                    RegistrationError.EmptyRole -> showMessage(getString(registrationError.messageResourceId))
                }
                return@setOnClickListener
            }

            setLoading(true, mRegisterBtn, progressBar)

            val role = when (roleId) {
                R.id.button_caretaker -> "caretaker"
                R.id.button_nurse -> "nurse"
                else -> ""
            }

            val request =
                RegisterRequest(
                    email = emailInput,
                    password = passwordInput,
                    name = nameInput,
                    role = role,
                )

            val call = ApiClient.apiService.register(request)

            call.enqueue(
                object : Callback<AuthResponse> {
                    override fun onResponse(
                        call: Call<AuthResponse>,
                        response: Response<AuthResponse>,
                    ) {
                        setLoading(false, mRegisterBtn, progressBar)
                        if (response.isSuccessful) {
                            showMessage(getString(R.string.registration_success))
                            NavigationService(this@RegisterActivity).toLogin()
                        } else {
                            val errorMsg = parseError(response)
                            showMessage(getString(R.string.registration_failure) + (if (errorMsg != null) ": $errorMsg" else ""))
                        }
                    }

                    override fun onFailure(
                        call: Call<AuthResponse>,
                        t: Throwable,
                    ) {
                        setLoading(false, mRegisterBtn, progressBar)
                        showMessage(getString(R.string.registration_failure) + ": ${t.localizedMessage}")
                    }
                },
            )
        }

        backToLoginButton.setOnClickListener {
            finish()
        }
    }

    private fun setLoading(isLoading: Boolean, button: Button, progressBar: ProgressBar) {
        button.isEnabled = !isLoading
        progressBar.isVisible = isLoading
    }

    private fun parseError(response: Response<*>): String? {
        return try {
            val errorResponse = Gson().fromJson(
                response.errorBody()?.string(),
                ApiErrorResponse::class.java,
            )
            errorResponse.apiError
        } catch (e: Exception) {
            null
        }
    }

    private fun validateInputs(
        rawEmail: String?,
        rawPassword: String?,
        rawConfirmedPassword: String?,
        rawName: String?,
        roleInput: Int,
    ): RegistrationError? {
        if (rawName.isNullOrEmpty()) {
            return RegistrationError.EmptyName
        }
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

        if (roleInput == android.view.View.NO_ID) {
            return RegistrationError.EmptyRole
        }

        return null
    }

    private fun showMessage(message: String) {
        Toast.makeText(this@RegisterActivity, message, Toast.LENGTH_SHORT).show()
    }
}
