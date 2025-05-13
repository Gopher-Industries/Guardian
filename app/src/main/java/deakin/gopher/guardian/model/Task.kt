package deakin.gopher.guardian.model

data class Task(
    var taskId: String = "",
    val title: String = "",
    val description: String = "",
    val assignedNurse: String = "",
    val priority: Priority,
    val completed: Boolean,
    val patientId: String = "",
    val dueDate: String = ""
)


enum class Priority {
    LOW,
    MEDIUM,
    HIGH,
}
