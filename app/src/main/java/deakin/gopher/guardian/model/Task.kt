package deakin.gopher.guardian.model

import com.google.gson.annotations.SerializedName

data class Task(
    @SerializedName(value = "taskId", alternate = ["_id", "id"])
    var taskId: String = "",
    @SerializedName("title")
    val title: String? = "",
    @SerializedName("description")
    val description: String = "",
    @SerializedName("assignedNurse")
    val assignedNurse: String = "",
    @SerializedName(value = "priority", alternate = ["priorityString"])
    private var _priorityString: String? = "MEDIUM",
    @SerializedName("patientId")
    val patientId: String? = "",
    @SerializedName("status")
    var status: String? = "pending",
    @SerializedName("dueDate")
    val dueDate: String? = null,
    @SerializedName("assigneeId")
    val assigneeId: String? = "",
) {
    val priorityString: String?
        get() = _priorityString

    var priority: Priority
        get() =
            when (_priorityString?.uppercase()) {
                "LOW" -> Priority.LOW
                "HIGH" -> Priority.HIGH
                else -> Priority.MEDIUM
            }
        set(value) {
            _priorityString = value.name
        }

    var completed: Boolean
        get() = status?.equals("completed", ignoreCase = true) == true
        set(value) {
            status = if (value) "completed" else "pending"
        }

    constructor(
        taskId: String = "",
        description: String = "",
        assignedNurse: String = "",
        priority: Priority = Priority.MEDIUM,
        patientId: String? = "",
        completed: Boolean = false,
    ) : this(
        taskId = taskId,
        title = "",
        description = description,
        assignedNurse = assignedNurse,
        _priorityString = priority.name,
        patientId = patientId,
        status = if (completed) "completed" else "pending",
        dueDate = null,
        assigneeId = "",
    )
}

enum class Priority {
    LOW,
    MEDIUM,
    HIGH,
}
