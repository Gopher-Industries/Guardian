package deakin.gopher.guardian.model

data class Task(
    var taskId: String = "",
    val description: String = "",
    val subDescription: String = "",
    val patientId: String = "",
    val isCompleted: Boolean = false,
)
