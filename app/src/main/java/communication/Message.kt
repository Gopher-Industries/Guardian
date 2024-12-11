package deakin.gopher.guardian.communication

import java.util.Date

data class Message(
    val senderId: String,
    val recipientId: String,
    val content: String,
    val date: String
)