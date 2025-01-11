package deakin.gopher.guardian.view.general

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.ApiErrorResponse
import deakin.gopher.guardian.model.BaseModel
import deakin.gopher.guardian.model.login.RoleName
import deakin.gopher.guardian.model.login.SessionManager
import deakin.gopher.guardian.services.NavigationService
import deakin.gopher.guardian.services.api.ApiClient
import deakin.gopher.guardian.view.hide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PinCodeActivity : AppCompatActivity() {
    private lateinit var userRole: RoleName
    private val userEmail = SessionManager.getCurrentUser().email

    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_enter_pin)
        userRole = intent.getSerializableExtra("role") as RoleName

        val pinDigit1 = findViewById<EditText>(R.id.pin_digit_1)
        val pinDigit2 = findViewById<EditText>(R.id.pin_digit_2)
        val pinDigit3 = findViewById<EditText>(R.id.pin_digit_3)
        val pinDigit4 = findViewById<EditText>(R.id.pin_digit_4)
        val pinDigit5 = findViewById<EditText>(R.id.pin_digit_5)
        val pinDigit6 = findViewById<EditText>(R.id.pin_digit_6)
        val submitButton = findViewById<Button>(R.id.loginBtn)
        val resendButton = findViewById<Button>(R.id.resendText)
        progressBar = findViewById<ProgressBar>(R.id.progressBar)

        submitButton.setOnClickListener {
            val pin =
                pinDigit1.text.toString() +
                    pinDigit2.text.toString() +
                    pinDigit3.text.toString() +
                    pinDigit4.text.toString() +
                    pinDigit5.text.toString() +
                    pinDigit6.text.toString()
            if (pin.length == 6) {
                validatePin(pin)
            } else {
                showMessage("Please enter a 6-digit PIN")
            }
        }

        resendButton.setOnClickListener {
            sendPin()
        }
        resendButton.performClick()
    }

    private fun sendPin() {
        val call = ApiClient.apiService.sendPin(userEmail)
        call.enqueue(
            object : Callback<BaseModel> {
                override fun onResponse(call: Call<BaseModel>, response: Response<BaseModel>) {
                    progressBar.hide()
                    if (response.isSuccessful && response.body() != null) {
                        showMessage(response.body()!!.apiMessage ?: "Pin sent tou your email")
                    } else {
                        // Handle error
                        val errorResponse =
                            Gson().fromJson(
                                response.errorBody()?.string(),
                                ApiErrorResponse::class.java,
                            )
                        showMessage(errorResponse.apiError ?: response.message())
                    }
                }

                override fun onFailure(call: Call<BaseModel>, t: Throwable) {
                    // Handle failure
                    progressBar.hide()
                    showMessage("Error sending PIN: ${t.message}")
                }
            },
        )
    }

    private fun validatePin(pin: String) {
        val call = ApiClient.apiService.verifyPin(userEmail, pin)
        call.enqueue(
            object : Callback<BaseModel> {
                override fun onResponse(call: Call<BaseModel>, response: Response<BaseModel>) {
                    progressBar.hide()
                    if (response.isSuccessful && response.body() != null) {
                        showMessage(response.body()!!.apiMessage ?: "Pin verified successfully")
                        NavigationService(this@PinCodeActivity).toHomeScreenForRole(userRole)
                        finish()
                    } else {
                        // Handle error
                        val errorResponse =
                            Gson().fromJson(
                                response.errorBody()?.string(),
                                ApiErrorResponse::class.java,
                            )
                        showMessage(errorResponse.apiError ?: response.message())
                    }
                }

                override fun onFailure(call: Call<BaseModel>, t: Throwable) {
                    // Handle failure
                    progressBar.hide()
                    showMessage("Error validating PIN: ${t.message}")
                }
            },
        )
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
