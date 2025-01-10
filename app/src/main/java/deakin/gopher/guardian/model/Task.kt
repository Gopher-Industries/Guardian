package deakin.gopher.guardian.model

enum class Priority {
    HIGH,
    MEDIUM,
    LOW,
}

data class Task(
    var taskId: String = "",
    var description: String = "",
    var assignedNurse: String = "",
    var priority: Priority = Priority.MEDIUM,
    var patientId: String = "",
    var taskSubDesc: String = "",
    var completed: Boolean = false,
)
