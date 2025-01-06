package deakin.gopher.guardian.view.general

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.messaging.FirebaseMessaging
import deakin.gopher.guardian.R
import deakin.gopher.guardian.adapter.MessageAdapter
import deakin.gopher.guardian.communication.Message
import deakin.gopher.guardian.model.login.SessionManager
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

        recyclerViewMessages = findViewById(R.id.recyclerViewMessages)
        editTextMessage = findViewById(R.id.editTextMessage)

        findViewById<Button>(R.id.getStartedButton).setOnClickListener {
            onGetStartedClick()
        }

        setupRecyclerView()
        loadMessages()

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

    private fun setupRecyclerView() {
        messageAdapter = MessageAdapter(messageList)
        recyclerViewMessages.layoutManager = LinearLayoutManager(this)
        recyclerViewMessages.adapter = messageAdapter
    }

    private fun loadMessages() {
        db.collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
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
            val userId: String = SessionManager.getUserId() ?: return

            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val formattedDate = dateFormat.format(Date())

            val message = Message(
                senderId = userId,
                recipientUserId = "recipientUserId",
                content = messageContent,
                date = formattedDate
            )

            db.collection("messages")
                .add(message)
                .addOnSuccessListener {
                    editTextMessage.text.clear()
                    loadMessages()
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
            Toast.makeText(this, "Password updated successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveNotificationPreferences() {
        val emailNotif = findViewById<Switch>(R.id.switch_email_notifications).isChecked
        val smsNotif = findViewById<Switch>(R.id.switch_sms_notifications).isChecked
        val pushNotif = findViewById<Switch>(R.id.switch_push_notifications).isChecked

        Toast.makeText(this, "Notification preferences saved successfully!", Toast.LENGTH_SHORT).show()
    }

    private fun onGetStartedClick() {
        startActivity(Intent(this, LoginActivity::class.java))
    }
}
