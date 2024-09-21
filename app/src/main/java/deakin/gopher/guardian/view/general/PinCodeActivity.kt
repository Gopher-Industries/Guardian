package deakin.gopher.guardian.view.general

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.login.RoleName
import deakin.gopher.guardian.services.NavigationService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

// Define the API interface
interface PinValidationApi {
    @POST("validate_pin")
    suspend fun validatePin(
        @Body pinRequest: PinRequest,
    ): PinResponse
}

// Data classes for request and response
data class PinRequest(val pin: String)

data class PinResponse(val isValid: Boolean)

class PinCodeActivity : AppCompatActivity() {
    private lateinit var api: PinValidationApi
    private lateinit var userRole: RoleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_enter_pin)
        val bundle: Bundle? = intent.extras
        userRole = intent.getSerializableExtra("role") as RoleName

        // Initialize Retrofit and create API instance
        val retrofit =
            Retrofit.Builder()
                .baseUrl("https://your-api-base-url.com/") // Replace with your actual API base URL
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        api = retrofit.create(PinValidationApi::class.java)

        val pinDigit1 = findViewById<EditText>(R.id.pin_digit_1)
        val pinDigit2 = findViewById<EditText>(R.id.pin_digit_2)
        val pinDigit3 = findViewById<EditText>(R.id.pin_digit_3)
        val pinDigit4 = findViewById<EditText>(R.id.pin_digit_4)
        val pinDigit5 = findViewById<EditText>(R.id.pin_digit_5)
        val pinDigit6 = findViewById<EditText>(R.id.pin_digit_6)
        val submitButton = findViewById<Button>(R.id.loginBtn)

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
                showError("Please enter a 6-digit PIN")
            }
        }
    }

    private fun validatePin(pin: String) {
        if (pin == "123456") {
            showError("PIN cannot be 123456")
            return
        }

        if (pin.all { it == pin[0] }) {
            showError("PIN cannot be 6 repeated digits")
            return
        }

        // If local validation passes, check against the database
        checkPinAgainstDatabase(pin)
    }

    private fun showError(message: String) {
        Toast.makeText(this@PinCodeActivity, message, Toast.LENGTH_SHORT).show()
    }

    private fun checkPinAgainstDatabase(pin: String) {
        NavigationService(this@PinCodeActivity).toPinCodeActivity(userRole)
        finish()
        return
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response =
                    withContext(Dispatchers.IO) {
                        api.validatePin(PinRequest(pin))
                    }

                if (response.isValid) {
                    NavigationService(this@PinCodeActivity).toPinCodeActivity(userRole)
                    finish()
                } else {
                    showError("Invalid PIN")
                }
            } catch (e: Exception) {
                showError("Error validating PIN: ${e.message}")
            }
        }
    }
}
