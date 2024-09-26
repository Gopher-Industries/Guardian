package deakin.gopher.guardian.view.general

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.isVisible
import com.amin.muteapp.api.ServiceBuilder
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.gson.Gson
import deakin.gopher.guardian.R
import deakin.gopher.guardian.api.nurse.model.NurseRegisterResponse
import deakin.gopher.guardian.api.nurse.param.NurseRegisterParam
import deakin.gopher.guardian.model.RegistrationStatusMessage
import deakin.gopher.guardian.model.login.EmailAddress
import deakin.gopher.guardian.model.login.Password
import deakin.gopher.guardian.model.register.RegisterRequest
import deakin.gopher.guardian.model.register.RegisterResponse
import deakin.gopher.guardian.model.register.RegistrationError
import deakin.gopher.guardian.model.register.User
import deakin.gopher.guardian.services.NavigationService
import deakin.gopher.guardian.services.api.ApiClient
import deakin.gopher.guardian.services.api.ApiService
import org.apache.commons.httpclient.HttpStatus
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.create

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
            val emailInput = mEmail.text.toString().trim { it <= ' ' }
            val passwordInput = mPassword.text.toString().trim { it <= ' ' }
            val passwordConfirmInput = passwordConfirmation.text.toString().trim { it <= ' ' }
            val nameInput = mFullName.text.toString().trim { it <= ' ' }
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
                    registrationError.messageResourceId,
                    Toast.LENGTH_LONG,
                ).show()
                return@setOnClickListener
            }

            progressBar.visibility = View.VISIBLE

            val emailAddress = EmailAddress(emailInput)
            val password = Password(passwordInput)
            val role = findViewById<MaterialButton>(roleInput).text.toString().lowercase()

            val nurseRegisterParam = NurseRegisterParam()
            nurseRegisterParam.name = mFullName.text.toString()
            nurseRegisterParam.email = emailAddress.emailAddress
            nurseRegisterParam.password = password.password


            val apiservice = ServiceBuilder.buildService(this)?.create(ApiService::class.java)
            val call = apiservice?.register(nurseRegisterParam)
            call?.enqueue(
                object : Callback<NurseRegisterResponse> {
                    override fun onResponse(
                        call: Call<NurseRegisterResponse>,
                        response: Response<NurseRegisterResponse>,
                    ) {
                        progressBar.isVisible = false
                        if (response.code() == HttpStatus.SC_CREATED && response.isSuccessful && response.body() != null) {
                            // Handle successful registration
                            Log.e("Responsee---->",Gson().toJson(response.body()))
                            response.body()!!.message?.let { it1 -> showMessage(it1) }
                            NavigationService(this@RegisterActivity).toLogin()
                        } else if (response.code() == HttpStatus.SC_BAD_REQUEST) {
                            // Handle error
                            val errorBody = response.errorBody()!!.string()
                            val jsonObject = JSONObject(errorBody)
                            val errorMessage = jsonObject.optString("error")
                            Toast.makeText(this@RegisterActivity, errorMessage, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

                    override fun onFailure(
                        call: Call<NurseRegisterResponse>,
                        t: Throwable,
                    ) {
                        // Handle failure
                        progressBar.isVisible = false
                        showMessage(RegistrationStatusMessage.Failure.toString() + " : ${t.message}")
                    }
                },
            )
        }

        backToLoginButton.setOnClickListener {
            NavigationService(this).toLogin()
        }
    }

    private fun validateInputs(
        rawEmail: String?,
        rawPassword: String?,
        rawConfirmedPassword: String?,
        rawName: String?,
        roleInput: Int,
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

        if (rawName.isNullOrEmpty()) {
            return RegistrationError.EmptyName
        }

        if (roleInput == View.NO_ID) {
            return RegistrationError.EmptyRole
        }

        return null
    }

    private fun showMessage(message: String) {
        Toast.makeText(this@RegisterActivity, message, Toast.LENGTH_SHORT).show()
    }
}
