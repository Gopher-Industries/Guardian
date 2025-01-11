package deakin.gopher.guardian.view.general

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import deakin.gopher.guardian.R

class SendMessageActivity : AppCompatActivity() {

    private lateinit var responseTitle: TextView
    private lateinit var commentEditText: EditText
    private lateinit var sendButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_message)

        // Initialize Views
        responseTitle = findViewById(R.id.responseTitle)
        commentEditText = findViewById(R.id.commentEditText)
        sendButton = findViewById(R.id.sendButton)

        // Retrieve Notification Info (Passed via Intent)
        val notificationMessage = intent.getStringExtra("notification_message")
        val notificationId = intent.getIntExtra("notification_id", -1)

        // Set the Response Title dynamically
        if (notificationMessage != null) {
            responseTitle.text = "Response to $notificationMessage"
        } else {
            responseTitle.text = "Response to the notification"
        }

        // Handle Send Button Click
        sendButton.setOnClickListener {
            val comment = commentEditText.text.toString()

            if (comment.isBlank()) {
                Toast.makeText(this, "Please enter a comment", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Simulate sending a response (API call can be added here)
            sendResponse(notificationId, comment)
        }
    }

    private fun sendResponse(notificationId: Int, comment: String) {
        // Simulate a successful response
        Toast.makeText(this, "Message sent!", Toast.LENGTH_SHORT).show()

        // Navigate to Success Page
        val intent = Intent(this, SuccessActivity::class.java)
        startActivity(intent)
        finish()
    }
}




