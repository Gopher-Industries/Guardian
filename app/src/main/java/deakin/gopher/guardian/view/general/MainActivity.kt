package deakin.gopher.guardian.view.general

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.login.SessionManager
import deakin.gopher.guardian.adapter.MessageAdapter
import deakin.gopher.guardian.communication.Message
import deakin.gopher.guardian.services.api.ApiClient
import com.google.gson.Gson
import okhttp3.ResponseBody
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : BaseActivity() {
    private lateinit var recyclerViewMessages: RecyclerView
    private lateinit var messageAdapter: MessageAdapter
    private val messageList = mutableListOf<Message>()
    private lateinit var editTextMessage: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Check if the user is logged in
        if (SessionManager.isLoggedIn) {
            try {
                val currentUser = SessionManager.getCurrentUser()
                val userId = SessionManager.getUserId()  // Get the userId
                Log.d("MainActivity", "Current user ID: $userId")
            } catch (e: Exception) {
                Log.e("MainActivity", "Error retrieving current user: ${e.message}")
            }
        } else {
            Log.d("MainActivity", "User not logged in")
        }

        // Existing button setup
        val getStartedButton = findViewById<Button>(R.id.getStartedButton)
        getStartedButton.setOnClickListener { onGetStartedClick() }

        // New chat components
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages)
        editTextMessage = findViewById(R.id.editTextMessage)
        setupRecyclerView()

        // Button to send messages
        findViewById<Button>(R.id.buttonSend).setOnClickListener { sendMessage() }

        loadMessages()  // Load existing messages
    }

    private fun setupRecyclerView() {
        messageAdapter = MessageAdapter(messageList)
        recyclerViewMessages.layoutManager = LinearLayoutManager(this)
        recyclerViewMessages.adapter = messageAdapter
    }

    private fun loadMessages() {
        val url = "https://guardian-backend-kz54.onrender.com/messages"
        // Use an HTTP client like Retrofit or OkHttp
        ApiClient.get(url, { response ->
            messageList.clear()
            val messages = response.toMessageList()  // Parse response into Message objects
            messageList.addAll(messages)
            messageAdapter.notifyDataSetChanged()
        }, { error ->
            Toast.makeText(this, "Error loading messages", Toast.LENGTH_SHORT).show()
        })
    }

    private fun sendMessage() {
        val messageContent: String = editTextMessage.text.toString().trim()

        if (messageContent.isNotEmpty()) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val formattedDate = dateFormat.format(Date())

            val recipientUserId = "recipientUserId" // Replace this with actual user ID logic
            val userId: String = SessionManager.getUserId() ?: run {
                Toast.makeText(this, "User ID is null", Toast.LENGTH_SHORT).show()
                return
            }

            val message = Message(userId, recipientUserId, messageContent, formattedDate)

            // Make the API call to send the message
            ApiClient.post("https://guardian-backend-kz54.onrender.com/messages", message, { response ->
                editTextMessage.text.clear()  // Clear the input field
                loadMessages()  // Refresh message list
            }, { error ->
                Toast.makeText(this, "Error sending message", Toast.LENGTH_SHORT).show()
            })
        } else {
            Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onGetStartedClick() {
        if (!SessionManager.isLoggedIn) {
            startActivity(Intent(this, LoginActivity::class.java))
        } else {
            startActivity(Intent(this, Homepage4caretaker::class.java))
        }
    }

    private fun ResponseBody.toMessageList(): List<Message> {
        val json = this.string() ?: return emptyList()  // Safely handle null string
        val gson = Gson()
        val type = object : TypeToken<List<Message>>() {}.type
        return gson.fromJson(json, type)
    }
}



