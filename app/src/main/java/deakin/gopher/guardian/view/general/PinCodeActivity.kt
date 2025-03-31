package deakin.gopher.guardian.view.general

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
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
            startResendCooldown()
        }
        resendButton.performClick()
    }

    private fun sendPin() {
        val call = ApiClient.apiService.sendPin(userEmail)
        call.enqueue(
            object : Callback<BaseModel<Unit>> { // Specify <Unit> as the type argument
                override fun onResponse(
                    call: Call<BaseModel<Unit>>,
                    response: Response<BaseModel<Unit>>,
                ) {
                    progressBar.hide()
                    if (response.isSuccessful) {
                        showMessage("Pin sent to your email")
                    } else {
                        val errorResponse =
                            Gson().fromJson(
                                response.errorBody()?.string(),
                                ApiErrorResponse::class.java,
                            )
                        showMessage(errorResponse.apiError ?: response.message())
                    }
                }

                override fun onFailure(
                    call: Call<BaseModel<Unit>>,
                    t: Throwable,
                ) {
                    progressBar.hide()
                    showMessage("Error sending PIN: ${t.message}")
                }
            },
        )
    }

    private fun validatePin(pin: String) {
        val call = ApiClient.apiService.verifyPin(userEmail, pin)
        call.enqueue(
            object : Callback<BaseModel<Unit>> { // Specify <Unit> as the type argument
                override fun onResponse(
                    call: Call<BaseModel<Unit>>,
                    response: Response<BaseModel<Unit>>,
                ) {
                    progressBar.hide()
                    if (response.isSuccessful) {
                        showMessage("Pin verified successfully")
                        NavigationService(this@PinCodeActivity).toHomeScreenForRole(userRole)
                        finish()
                    } else {
                        val errorResponse =
                            Gson().fromJson(
                                response.errorBody()?.string(),
                                ApiErrorResponse::class.java,
                            )
                        showMessage(errorResponse.apiError ?: response.message())
                    }
                }

                override fun onFailure(
                    call: Call<BaseModel<Unit>>,
                    t: Throwable,
                ) {
                    progressBar.hide()
                    showMessage("Error validating PIN: ${t.message}")
                }
            },
        )
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun startResendCooldown() {
        val countdownTimerContainer = findViewById<LinearLayoutCompat>(R.id.containerCountdown)
        val countdownTimer = findViewById<TextView>(R.id.countdownTimer)
        val resendButton = findViewById<Button>(R.id.resendText)

        // Disable the resend button
        resendButton.isEnabled = false
        resendButton.setTextColor(resources.getColor(R.color.gray2))
        countdownTimerContainer.visibility = View.VISIBLE

        // Countdown for 1 minute 30 seconds (90,000ms)
        object : CountDownTimer(90000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                val countdownText =
                    resources.getQuantityString(
                        R.plurals.resend_countdown_seconds,
                        secondsLeft.toInt(),
                        secondsLeft,
                    )
                countdownTimer.text = countdownText
            }

            override fun onFinish() {
                // Enable the resend button
                resendButton.isEnabled = true
                resendButton.setTextColor(resources.getColor(R.color.TG_blue))
                countdownTimerContainer.visibility = View.GONE
            }
        }.start()
    }
}
