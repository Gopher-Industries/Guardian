package deakin.gopher.guardian.model

data class Task(
    var taskId: String = "",
    val description: String = "",
    val assignedNurse: String = "",
    val priority: Priority = Priority.MEDIUM,
    val patientId: String? = "",
    val completed: Boolean = false,
)

enum class Priority {
    LOW,
    MEDIUM,
    HIGH,
}
