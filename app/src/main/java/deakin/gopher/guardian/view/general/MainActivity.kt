package deakin.gopher.guardian.view.general

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.login.SessionManager
import deakin.gopher.guardian.services.EmailPasswordAuthService
import deakin.gopher.guardian.adapter.MessageAdapter
import deakin.gopher.guardian.communication.Message // Import Message class

import java.util.Date

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

        // Firebase Messaging setup
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task: Task<String?> ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("FCM Token", token ?: "Token is null")
            } else {
                Log.w("MainActivity", "Fetching FCM registration token failed", task.exception)
            }
        }

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
        db.collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .limit(20)  // Limit the number of messages for better performance
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
                    val timestamp = document.getDate("timestamp")
                    messageList.add(Message(sender, receiver, messageContent, timestamp))
                }
                messageAdapter.notifyDataSetChanged()
            }
    }

    private fun sendMessage() {
        val messageContent = editTextMessage.text.toString().trim()
        if (messageContent.isNotEmpty()) {
            val userId = SessionManager.getUserId()  // Get the user ID using the SessionManager method
            if (userId != null) {
                val message = Message(userId, "recipientUserId", messageContent, Date())
                db.collection("messages")
                    .add(message)
                    .addOnSuccessListener {
                        editTextMessage.text.clear()  // Clear input field
                        loadMessages()  // Refresh message list
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error sending message", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "User is not logged in", Toast.LENGTH_SHORT).show()
            }
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

    private fun onLogoutClick() {
        EmailPasswordAuthService.signOut(this)
        finish()
    }
}



