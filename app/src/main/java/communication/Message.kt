package deakin.gopher.guardian.communication

import java.util.Date

data class Message(
    val senderId: String,
    val recipientUserId: String,
    val content: String,
    val date: String
)