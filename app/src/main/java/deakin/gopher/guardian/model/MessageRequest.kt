package deakin.gopher.guardian.model

data class MessageRequest(
    val notificationId: Int,  // The ID of the notification being responded to
    val message: String       // The message content
)
