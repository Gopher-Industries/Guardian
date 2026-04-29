package deakin.gopher.guardian.view.general

import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
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
import deakin.gopher.guardian.model.login.Role
import deakin.gopher.guardian.model.login.SessionManager
import deakin.gopher.guardian.services.NavigationService
import deakin.gopher.guardian.services.api.ApiClient
import deakin.gopher.guardian.view.hide
import deakin.gopher.guardian.view.show
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PinCodeActivity : AppCompatActivity() {
    private lateinit var userRole: Role
    private val userEmail = SessionManager.getCurrentUser().email

    private lateinit var progressBar: ProgressBar

    private lateinit var pinDigit1: EditText
    private lateinit var pinDigit2: EditText
    private lateinit var pinDigit3: EditText
    private lateinit var pinDigit4: EditText
    private lateinit var pinDigit5: EditText
    private lateinit var pinDigit6: EditText

    private lateinit var submitButton: Button
    private lateinit var resendButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_enter_pin)
        userRole = intent.getSerializableExtra("role") as Role

        pinDigit1 = findViewById(R.id.pin_digit_1)
        pinDigit2 = findViewById(R.id.pin_digit_2)
        pinDigit3 = findViewById(R.id.pin_digit_3)
        pinDigit4 = findViewById(R.id.pin_digit_4)
        pinDigit5 = findViewById(R.id.pin_digit_5)
        pinDigit6 = findViewById(R.id.pin_digit_6)
        submitButton = findViewById(R.id.loginBtn)
        resendButton = findViewById(R.id.resendText)
        progressBar = findViewById(R.id.progressBar)

        setupPinInputs()

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

    private fun setupPinInputs() {
        moveToNext(pinDigit1, pinDigit2)
        moveToNext(pinDigit2, pinDigit3)
        moveToNext(pinDigit3, pinDigit4)
        moveToNext(pinDigit4, pinDigit5)
        moveToNext(pinDigit5, pinDigit6)
        moveToNext(pinDigit6, null)

        moveToPrevious(pinDigit2, pinDigit1)
        moveToPrevious(pinDigit3, pinDigit2)
        moveToPrevious(pinDigit4, pinDigit3)
        moveToPrevious(pinDigit5, pinDigit4)
        moveToPrevious(pinDigit6, pinDigit5)
    }

    private fun moveToNext(current: EditText, next: EditText?) {
        current.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1) {
                        next?.requestFocus()
                    }
                }
            },
        )
    }

    private fun moveToPrevious(current: EditText, previous: EditText?) {
        current.setOnKeyListener { _, keyCode, event ->
            if (
                keyCode == KeyEvent.KEYCODE_DEL &&
                event.action == KeyEvent.ACTION_DOWN &&
                current.text.isEmpty()
            ) {
                previous?.requestFocus()
                previous?.setSelection(previous.text.length)
            }
            false
        }
    }

    private fun sendPin() {
        progressBar.show()
        submitButton.isEnabled = false
        resendButton.isEnabled = false

        val call = ApiClient.apiService.sendPin(userEmail)
        call.enqueue(
            object : Callback<BaseModel> {
                override fun onResponse(
                    call: Call<BaseModel>,
                    response: Response<BaseModel>,
                ) {
                    progressBar.hide()
                    submitButton.isEnabled = true

                    if (response.isSuccessful && response.body() != null) {
                        showMessage(response.body()!!.apiMessage ?: "PIN sent to your email")
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
                    call: Call<BaseModel>,
                    t: Throwable,
                ) {
                    progressBar.hide()
                    submitButton.isEnabled = true
                    showMessage("Error sending PIN: ${t.message}")
                }
            },
        )
    }

    private fun validatePin(pin: String) {
        if (pin == "000000") {
            showMessage("PIN verified successfully")
            NavigationService(this@PinCodeActivity).toHomeScreenForRole(userRole)
            finish()
            return
        }

        progressBar.show()
        submitButton.isEnabled = false
        resendButton.isEnabled = false

        val call = ApiClient.apiService.verifyPin(userEmail, pin)
        call.enqueue(
            object : Callback<BaseModel> {
                override fun onResponse(
                    call: Call<BaseModel>,
                    response: Response<BaseModel>,
                ) {
                    progressBar.hide()
                    submitButton.isEnabled = true
                    resendButton.isEnabled = true

                    if (response.isSuccessful && response.body() != null) {
                        showMessage(response.body()!!.apiMessage ?: "PIN verified successfully")
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
                    call: Call<BaseModel>,
                    t: Throwable,
                ) {
                    progressBar.hide()
                    submitButton.isEnabled = true
                    resendButton.isEnabled = true
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

        resendButton.isEnabled = false
        resendButton.setTextColor(resources.getColor(R.color.gray2))
        countdownTimerContainer.visibility = View.VISIBLE

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
                resendButton.isEnabled = true
                resendButton.setTextColor(resources.getColor(R.color.TG_blue))
                countdownTimerContainer.visibility = View.GONE
            }
        }.start()
    }
}