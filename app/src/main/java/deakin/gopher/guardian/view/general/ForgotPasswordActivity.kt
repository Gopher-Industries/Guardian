package deakin.gopher.guardian.view.general

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.ApiErrorResponse
import deakin.gopher.guardian.model.BaseModel
import deakin.gopher.guardian.model.login.EmailAddress
import deakin.gopher.guardian.services.api.ApiClient
import deakin.gopher.guardian.view.hide
import deakin.gopher.guardian.view.show
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPasswordActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        val emailEditText: EditText = findViewById(R.id.emailEditText)
        val emailLayout: TextInputLayout = findViewById(R.id.emailTextInputLayout)
        val resetButton: Button = findViewById(R.id.resetButton)
        val backToLoginButton: Button = findViewById(R.id.backToLoginButton)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        val instructionText: TextView = findViewById(R.id.instructionText)
        val statusIcon: ImageView = findViewById(R.id.statusIcon)

        emailEditText.addTextChangedListener { emailLayout.error = null }

        resetButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()

            if (email.isEmpty()) {
                emailLayout.error = getString(R.string.validation_empty_email)
                return@setOnClickListener
            }

            if (!EmailAddress(email).isValid()) {
                emailLayout.error = getString(R.string.validation_invalid_email_address)
                return@setOnClickListener
            }

            setLoading(true, resetButton, progressBar)

            val call = ApiClient.apiService.requestPasswordReset(email)
            call.enqueue(object : Callback<BaseModel> {
                override fun onResponse(call: Call<BaseModel>, response: Response<BaseModel>) {
                    setLoading(false, resetButton, progressBar)
                    if (response.isSuccessful) {
                        // Show success state
                        instructionText.text = response.body()?.apiMessage ?: getString(R.string.toast_reset_link_sent_to_your_email)
                        emailLayout.visibility = View.GONE
                        resetButton.visibility = View.GONE
                        statusIcon.visibility = View.VISIBLE
                        // Change icon to something like a success check if available, 
                        // using agedcare_icon as placeholder
                    } else {
                        val errorResponse = parseError(response)
                        showMessage(errorResponse ?: response.message())
                    }
                }

                override fun onFailure(call: Call<BaseModel>, t: Throwable) {
                    setLoading(false, resetButton, progressBar)
                    showMessage(getString(R.string.toast_error_reset_link_is_not_sent_reason, t.localizedMessage))
                }
            })
        }

        backToLoginButton.setOnClickListener {
            finish()
        }
    }

    private fun setLoading(isLoading: Boolean, button: Button, progressBar: ProgressBar) {
        button.isEnabled = !isLoading
        if (isLoading) progressBar.show() else progressBar.hide()
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

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
