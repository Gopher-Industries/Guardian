package deakin.gopher.guardian.view.general

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import deakin.gopher.guardian.R
import deakin.gopher.guardian.adapter.ChatAdapter
import deakin.gopher.guardian.model.chatMessage

class ChatBotActivity : BaseActivity() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageInput: EditText
    private lateinit var sendButton: Button
    private val messages = mutableListOf<chatMessage>()
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var sharedPreferences: SharedPreferences

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Load theme from SharedPreferences
        sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
        val isNightMode = sharedPreferences.getBoolean("night_mode", false)

        if (isNightMode) {
            setTheme(R.style.Theme_TeamGuardians_Dark)
        } else {
            setTheme(R.style.Theme_TeamGuardians)
        }

        setContentView(R.layout.activity_chat_bot)

        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageInput = findViewById(R.id.userMessageInput)
        sendButton = findViewById(R.id.sendButton)

        // Setup RecyclerView
        chatAdapter = ChatAdapter(messages)
        chatRecyclerView.adapter = chatAdapter
        chatRecyclerView.layoutManager = LinearLayoutManager(this)

        sendButton.setOnClickListener {
            val userMessage = messageInput.text.toString().trim()
            if (userMessage.isNotEmpty()) {
                addMessage(userMessage, isUser = true)
                messageInput.text.clear()

                // Handle theme toggle before generating AI response
                if (checkForNightModeToggle(userMessage)) {
                    return@setOnClickListener
                }

                // Generate AI response
                val response = generateAIResponse(userMessage)
                addMessage(response.first, isUser = false)

                // If response contains a redirection, display the button
                response.second?.let { buttonLabel ->
                    showRedirectButton(buttonLabel)
                }
            } else {
                Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addMessage(text: String, isUser: Boolean) {
        messages.add(chatMessage(text, isUser))
        chatAdapter.notifyItemInserted(messages.size - 1)
        chatRecyclerView.scrollToPosition(messages.size - 1)
    }

    private fun generateAIResponse(message: String): Pair<String, String?> {
        val lowerMessage = message.lowercase()

        return when {
            lowerMessage.matches(Regex(".*\\b(h+e*l+o+|h+i+|heyy*)\\b.*")) ->
                Pair("Hi there! How can I assist you today?", null)

            lowerMessage.matches(Regex(".*\\b(h+e*l+p+|s+u+p+p*o+r+t+)\\b.*")) ->
                Pair("Sure! I’m here to help. Please tell me your concern.", null)

            lowerMessage.contains("can't find page") || lowerMessage.contains("where is the page") ->
                Pair("It seems like you're having trouble finding the page. Would you like to go to the settings page?", "Go to Setting Page")

            lowerMessage.contains("profile") ->
                Pair("You can find your profile details here!", null)

            else -> Pair("I'm still learning. Can you try rephrasing that?", null)
        }
    }

    private fun showRedirectButton(buttonLabel: String) {
        val button = Button(this)
        button.text = buttonLabel
        button.setOnClickListener {
            when (buttonLabel) {
                "Go to Setting Page" -> {
                    val intent = Intent(this, Setting::class.java)
                    startActivity(intent)
                }
                else -> {
                    Toast.makeText(this, "Redirect functionality not defined for this page", Toast.LENGTH_SHORT).show()
                }
            }
        }

        messages.add(chatMessage("Click the button below to be redirected:", false))
        messages.add(chatMessage(button.text.toString(), false))
        chatAdapter.notifyItemInserted(messages.size - 1)
        chatRecyclerView.scrollToPosition(messages.size - 1)
    }

    private fun checkForNightModeToggle(userMessage: String): Boolean {
        val lower = userMessage.lowercase()

        // Regex patterns
        val enableNightModePattern = Regex("(?i)(enable|turn on|switch to|activate).*(night|dark)\\s*mode")
        val disableNightModePattern = Regex("(?i)(disable|turn off|switch to|activate).*(light)\\s*mode")
        val togglePattern = Regex("(?i)(toggle|switch)\\s+(theme|mode|night\\s*mode)")

        val isNightMode = sharedPreferences.getBoolean("night_mode", false)
        var newMode: Boolean? = null

        when {
            enableNightModePattern.containsMatchIn(lower) -> {
                if (!isNightMode) newMode = true
            }
            disableNightModePattern.containsMatchIn(lower) -> {
                if (isNightMode) newMode = false
            }
            togglePattern.containsMatchIn(lower) -> {
                newMode = !isNightMode
            }
        }

        if (newMode != null) {
            // Save new theme preference
            sharedPreferences.edit().putBoolean("night_mode", newMode).apply()

            // Notify and restart activity
            addMessage(
                if (newMode) "Switching to night mode... Restarting to apply changes."
                else "Switching to light mode... Restarting to apply changes.",
                isUser = false
            )

            chatRecyclerView.postDelayed({
                AppCompatDelegate.setDefaultNightMode(
                    if (newMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
                )
                recreate()
            }, 1000)

            return true
        }

        return false
    }

}
