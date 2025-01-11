package deakin.gopher.guardian.view.general

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import deakin.gopher.guardian.R
import deakin.gopher.guardian.adapter.MessageAdapter
import deakin.gopher.guardian.adapter.CarePlanAdapter
import deakin.gopher.guardian.communication.Message
import deakin.gopher.guardian.model.CarePlan
import deakin.gopher.guardian.services.api.ApiClient
import deakin.gopher.guardian.services.api.ApiService
import deakin.gopher.guardian.model.login.SessionManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import deakin.gopher.guardian.model.MessageResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : BaseActivity() {
    // Firebase variables (retained but not actively used)
    private val db = FirebaseFirestore.getInstance()

    // API variables for messages
    private lateinit var recyclerViewMessages: RecyclerView
    private lateinit var messageAdapter: MessageAdapter
    private val messageList = mutableListOf<Message>()

    // API variables for care plans
    private lateinit var recyclerViewCarePlans: RecyclerView
    private lateinit var carePlanAdapter: CarePlanAdapter
    private val carePlanList = mutableListOf<CarePlan>()

    // API clien
    private val apiService: ApiService = ApiClient.apiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // API message setup
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages)
        setupMessageRecyclerView()
        loadMessagesFromApi()

        // API care plan setup
        recyclerViewCarePlans = findViewById(R.id.recyclerViewCarePlans)
        setupCarePlanRecyclerView()
        loadCarePlansFromApi()

        // Button click listeners
        findViewById<Button>(R.id.getStartedButton).setOnClickListener {
            onGetStartedClick()
        }
        findViewById<Button>(R.id.buttonSend).setOnClickListener {
            sendMessage()
        }
        findViewById<Button>(R.id.btn_save_profile).setOnClickListener {
            saveProfile()
        }
        findViewById<Button>(R.id.btn_save_password).setOnClickListener {
            changePassword()
        }
        findViewById<Button>(R.id.btn_save_notifications).setOnClickListener {
            saveNotificationPreferences()
        }
    }

    // API message setup
    private fun setupMessageRecyclerView() {
        messageAdapter = MessageAdapter(messageList)
        recyclerViewMessages.layoutManager = LinearLayoutManager(this)
        recyclerViewMessages.adapter = messageAdapter
    }

    private fun loadMessagesFromApi() {
        apiService.getMessages().enqueue(object : Callback<List<Message>> {
            override fun onResponse(call: Call<List<Message>>, response: Response<List<Message>>) {
                if (response.isSuccessful && response.body() != null) {
                    messageList.clear()
                    messageList.addAll(response.body()!!)
                    messageAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@MainActivity, "Failed to load messages", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Message>>, t: Throwable) {
                Log.e("MainActivity", "Error fetching messages: ${t.message}")
                Toast.makeText(this@MainActivity, "Error fetching messages", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // API care plan setup
    private fun setupCarePlanRecyclerView() {
        carePlanAdapter = CarePlanAdapter(carePlanList)
        recyclerViewCarePlans.layoutManager = LinearLayoutManager(this)
        recyclerViewCarePlans.adapter = carePlanAdapter
    }

    private fun loadCarePlansFromApi() {
        apiService.getCarePlans().enqueue(object : Callback<List<CarePlan>> {
            override fun onResponse(call: Call<List<CarePlan>>, response: Response<List<CarePlan>>) {
                if (response.isSuccessful && response.body() != null) {
                    carePlanList.clear()
                    carePlanList.addAll(response.body()!!)
                    carePlanAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@MainActivity, "Failed to load care plans", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<CarePlan>>, t: Throwable) {
                Log.e("MainActivity", "Error fetching care plans: ${t.message}")
                Toast.makeText(this@MainActivity, "Error fetching care plans", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Send message via API
    private fun sendMessage() {
        val messageContent = findViewById<EditText>(R.id.editTextMessage).text.toString().trim()
        if (messageContent.isNotEmpty()) {
            val userId = SessionManager.getUserId() ?: return
            val message = Message(senderId = userId, recipientUserId= "recipientUserId", messageContent = messageContent)

            apiService.sendMessage(message).enqueue(object : Callback<MessageResponse> {

                override fun onResponse(p0: Call<MessageResponse>, response: Response<MessageResponse>) {
                    if (response.isSuccessful) {
                        findViewById<EditText>(R.id.editTextMessage).text.clear()
                        loadMessagesFromApi()
                        Toast.makeText(this@MainActivity, "Message sent successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@MainActivity, "Failed to send message", Toast.LENGTH_SHORT).show()
                    }

                }

                override fun onFailure(p0: Call<MessageResponse>, p1: Throwable) {
                    Log.e("MainActivity", "Error sending message: ${p1.message}")
                    Toast.makeText(this@MainActivity, "Error sending message", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show()
        }
    }

    private fun changePassword() {
        val oldPassword = findViewById<EditText>(R.id.et_old_password).text.toString()
        val newPassword = findViewById<EditText>(R.id.et_new_password).text.toString()
        val confirmPassword = findViewById<EditText>(R.id.et_confirm_password).text.toString()

        if (newPassword != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Password updated successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveNotificationPreferences() {
        val emailNotif = findViewById<Switch>(R.id.switch_email_notifications).isChecked
        val smsNotif = findViewById<Switch>(R.id.switch_sms_notifications).isChecked
        val pushNotif = findViewById<Switch>(R.id.switch_push_notifications).isChecked

        Toast.makeText(this, "Notification preferences saved successfully!", Toast.LENGTH_SHORT).show()
    }

    private fun saveProfile() {
        val name = findViewById<EditText>(R.id.et_name).text.toString()
        val email = findViewById<EditText>(R.id.et_email).text.toString()
        val phone = findViewById<EditText>(R.id.et_phone).text.toString()

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Profile saved successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onGetStartedClick() {
        try {
            val intent = Intent(this, PatientListActivity::class.java)
            startActivity(intent)
        } catch (e: Exception) {
            Log.e("MainActivity", "Error navigating to PatientListActivity", e)
            Toast.makeText(this, "Navigation Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
