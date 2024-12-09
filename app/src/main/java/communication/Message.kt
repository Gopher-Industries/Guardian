package deakin.gopher.guardian.communication

import java.util.Date

data class Message(
    var senderId: String,
    var receiverId: String,
    var messageContent: String,
    var timestamp: Date?
)

