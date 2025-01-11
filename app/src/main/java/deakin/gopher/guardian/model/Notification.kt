package deakin.gopher.guardian.model

data class Notification(
    val id: Int,
    val message: String,
    val date: String,
    val isNew: Boolean
)
