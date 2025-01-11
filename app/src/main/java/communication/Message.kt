package deakin.gopher.guardian.communication

data class Message(
    val senderId: String,
    val recipientUserId: String,
    val messageContent: String,
    val timestamp: Long = 0L,
)
