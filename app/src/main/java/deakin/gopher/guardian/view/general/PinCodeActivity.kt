package deakin.gopher.guardian.view.general

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import com.google.gson.Gson
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.ApiErrorResponse
import deakin.gopher.guardian.model.BaseModel
import deakin.gopher.guardian.model.login.Role
import deakin.gopher.guardian.model.login.SessionManager
import deakin.gopher.guardian.services.NavigationService
import deakin.gopher.guardian.services.api.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PinCodeActivity : AppCompatActivity() {
    private lateinit var userRole: Role
    private val userEmail = SessionManager.getCurrentUser().email

    private lateinit var progressBar: ProgressBar
    private lateinit var submitButton: Button
    private lateinit var resendButton: Button
    private lateinit var pinDigits: List<EditText>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_enter_pin)
        userRole = intent.getSerializableExtra("role") as Role

        pinDigits = listOf(
            findViewById(R.id.pin_digit_1),
            findViewById(R.id.pin_digit_2),
            findViewById(R.id.pin_digit_3),
            findViewById(R.id.pin_digit_4),
            findViewById(R.id.pin_digit_5),
            findViewById(R.id.pin_digit_6)
        )
        
        submitButton = findViewById(R.id.loginBtn)
        resendButton = findViewById(R.id.resendText)
        progressBar = findViewById(R.id.progressBar)

        setupPinInputs()

        submitButton.setOnClickListener {
            val pin = pinDigits.joinToString("") { it.text.toString() }
            if (pin.length == 6) {
                validatePin(pin)
            } else {
                showMessage(getString(R.string.error_enter_6_digit_pin))
            }
        }

        resendButton.setOnClickListener {
            sendPin()
            startResendCooldown()
        }
        
        // Initial PIN request
        sendPin()
        startResendCooldown()
        
        // Focus first digit and show keyboard
        pinDigits[0].postDelayed({
            pinDigits[0].requestFocus()
            showKeyboard(pinDigits[0])
        }, 300)
    }

    private fun setupPinInputs() {
        for (i in pinDigits.indices) {
            pinDigits[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1) {
                        if (i < pinDigits.size - 1) {
                            pinDigits[i + 1].requestFocus()
                        }
                    }
                    updateSubmitButtonState()
                }
            })

            pinDigits[i].setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                    if (pinDigits[i].text.isEmpty() && i > 0) {
                        pinDigits[i - 1].requestFocus()
                        pinDigits[i - 1].text.clear()
                        return@setOnKeyListener true
                    }
                }
                false
            }
        }
    }

    private fun updateSubmitButtonState() {
        val isComplete = pinDigits.all { it.text.length == 1 }
        submitButton.isEnabled = isComplete
    }

    private fun sendPin() {
        setLoading(true)
        val call = ApiClient.apiService.sendPin(userEmail)
        call.enqueue(
            object : Callback<BaseModel> {
                override fun onResponse(
                    call: Call<BaseModel>,
                    response: Response<BaseModel>,
                ) {
                    setLoading(false)
                    if (response.isSuccessful && response.body() != null) {
                        showMessage(response.body()!!.apiMessage ?: getString(R.string.toast_pin_sent))
                    } else {
                        val errorResponse = parseError(response)
                        showMessage(errorResponse ?: response.message())
                    }
                }

                override fun onFailure(call: Call<BaseModel>, t: Throwable) {
                    setLoading(false)
                    showMessage(getString(R.string.error_sending_pin, t.localizedMessage))
                }
            },
        )
    }

    private fun validatePin(pin: String) {
        setLoading(true)
        val call = ApiClient.apiService.verifyPin(userEmail, pin)
        call.enqueue(
            object : Callback<BaseModel> {
                override fun onResponse(
                    call: Call<BaseModel>,
                    response: Response<BaseModel>,
                ) {
                    setLoading(false)
                    if (response.isSuccessful && response.body() != null) {
                        showMessage(response.body()!!.apiMessage ?: getString(R.string.toast_pin_verified))
                        NavigationService(this@PinCodeActivity).toHomeScreenForRole(userRole)
                        finish()
                    } else {
                        val errorResponse = parseError(response)
                        showMessage(errorResponse ?: response.message())
                        clearPin()
                    }
                }

                override fun onFailure(call: Call<BaseModel>, t: Throwable) {
                    setLoading(false)
                    showMessage(getString(R.string.error_validating_pin, t.localizedMessage))
                }
            },
        )
    }

    private fun clearPin() {
        pinDigits.forEach { it.text.clear() }
        pinDigits[0].requestFocus()
    }

    private fun setLoading(isLoading: Boolean) {
        progressBar.isVisible = isLoading
        submitButton.isEnabled = !isLoading && pinDigits.all { it.text.length == 1 }
        pinDigits.forEach { it.isEnabled = !isLoading }
        if (isLoading) {
            hideKeyboard()
        }
    }

    private fun showKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
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

    private fun startResendCooldown() {
        val countdownTimerContainer = findViewById<LinearLayoutCompat>(R.id.containerCountdown)
        val countdownTimer = findViewById<TextView>(R.id.countdownTimer)

        resendButton.isEnabled = false
        resendButton.alpha = 0.5f
        countdownTimerContainer.visibility = View.VISIBLE

        object : CountDownTimer(90000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                val countdownText = resources.getQuantityString(
                    R.plurals.resend_countdown_seconds,
                    secondsLeft.toInt(),
                    secondsLeft
                )
                countdownTimer.text = countdownText
            }

            override fun onFinish() {
                resendButton.isEnabled = true
                resendButton.alpha = 1.0f
                countdownTimerContainer.visibility = View.GONE
            }
        }.start()
    }
}
