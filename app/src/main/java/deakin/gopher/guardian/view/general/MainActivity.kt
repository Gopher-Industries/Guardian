package deakin.gopher.guardian.view.general

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.messaging.FirebaseMessaging
import deakin.gopher.guardian.R
import deakin.gopher.guardian.adapter.MessageAdapter
import deakin.gopher.guardian.communication.Message
import deakin.gopher.guardian.model.login.SessionManager
import deakin.gopher.guardian.services.EmailPasswordAuthService
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : BaseActivity() {
    private lateinit var recyclerViewMessages: RecyclerView
    private lateinit var messageAdapter: MessageAdapter
    private val messageList = mutableListOf<Message>()
    private lateinit var editTextMessage: EditText
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Check if the user is logged in
        if (SessionManager.isLoggedIn) {
            try {
                val userId = SessionManager.getUserId() ?: "Unknown User"
                Log.d("MainActivity", "Current user ID: $userId")
            } catch (e: Exception) {
                Log.e("MainActivity", "Error retrieving current user: ${e.message}")
            }
        } else {
            Log.d("MainActivity", "User not logged in")
        }

        // Setup UI components
        findViewById<Button>(R.id.getStartedButton).setOnClickListener { onGetStartedClick() }

        recyclerViewMessages = findViewById(R.id.recyclerViewMessages)
        editTextMessage = findViewById(R.id.editTextMessage)
        setupRecyclerView()

        findViewById<Button>(R.id.buttonSend).setOnClickListener { sendMessage() }
        findViewById<Button>(R.id.btn_save_profile).setOnClickListener { saveProfile() }
        findViewById<Button>(R.id.btn_save_password).setOnClickListener { changePassword() }
        findViewById<Button>(R.id.btn_save_notifications).setOnClickListener { saveNotificationPreferences() }

        // Firebase Messaging setup
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task: Task<String?> ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("FCM Token", token ?: "Token is null")
            } else {
                Log.w("MainActivity", "Fetching FCM registration token failed", task.exception)
            }
        }

        loadMessages() // Load existing messages
    }

    private fun setupRecyclerView() {
        messageAdapter = MessageAdapter(messageList)
        recyclerViewMessages.layoutManager = LinearLayoutManager(this)
        recyclerViewMessages.adapter = messageAdapter
    }

    private fun loadMessages() {
        db.collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .limit(20)
            .addSnapshotListener { querySnapshot, e ->
                if (e != null) {
                    Toast.makeText(this, "Error loading messages", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }
                messageList.clear()
                querySnapshot?.forEach { document ->
                    val sender = document.getString("senderId") ?: ""
                    val receiver = document.getString("receiverId") ?: ""
                    val messageContent = document.getString("messageContent") ?: ""
                    val timestamp = document.getString("timestamp") ?: ""
                    messageList.add(Message(sender, receiver, messageContent, timestamp))
                }
                messageAdapter.notifyDataSetChanged()
            }
    }

    private fun sendMessage() {
        val messageContent = editTextMessage.text.toString().trim()
        if (messageContent.isNotEmpty()) {
            val userId: String = SessionManager.getUserId() ?: run {
                Toast.makeText(this, "User ID is null", Toast.LENGTH_SHORT).show()
                return
            }

            // Format the current date as a string
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val formattedDate = dateFormat.format(Date())

            // Create a new Message object
            val message = Message(
                senderId = userId,
                recipientUserId = "recipientUserId", // Replace with actual recipient logic
                content = messageContent,
                date = formattedDate
            )

            // Add message to Firestore
            db.collection("messages")
                .add(message)
                .addOnSuccessListener {
                    editTextMessage.text.clear() // Clear the input field
                    Toast.makeText(this, "Message sent successfully!", Toast.LENGTH_SHORT).show()
                    loadMessages() // Reload messages to reflect the new message
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error sending message", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveProfile() {
        val name = findViewById<EditText>(R.id.et_name).text.toString()
        val email = findViewById<EditText>(R.id.et_email).text.toString()
        val phone = findViewById<EditText>(R.id.et_phone).text.toString()

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
        } else {
            // Save profile details (Firebase or API integration)
            Toast.makeText(this, "Profile saved successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun changePassword() {
        val oldPassword = findViewById<EditText>(R.id.et_old_password).text.toString()
        val newPassword = findViewById<EditText>(R.id.et_new_password).text.toString()
        val confirmPassword = findViewById<EditText>(R.id.et_confirm_password).text.toString()

        if (newPassword != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
        } else {
            // Change password logic (Firebase or API integration)
            Toast.makeText(this, "Password updated successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveNotificationPreferences() {
        val emailNotif = findViewById<Switch>(R.id.switch_email_notifications).isChecked
        val smsNotif = findViewById<Switch>(R.id.switch_sms_notifications).isChecked
        val pushNotif = findViewById<Switch>(R.id.switch_push_notifications).isChecked

        // Save preferences logic (Firebase or API integration)
        Toast.makeText(this, "Notification preferences saved successfully!", Toast.LENGTH_SHORT).show()
    }

    private fun onGetStartedClick() {
        if (!SessionManager.isLoggedIn) {
            startActivity(Intent(this, LoginActivity::class.java))
        } else {
            startActivity(Intent(this, Homepage4caretaker::class.java))
        }
    }

    private fun onLogoutClick() {
        EmailPasswordAuthService.signOut(this)
        finish()
    }
}
