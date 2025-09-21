// Fixed TaskModels.kt without JVM signature clash
package deakin.gopher.guardian.model.calendar

import com.google.gson.annotations.SerializedName
import deakin.gopher.guardian.model.BaseModel

// Request Models (these don't need to extend BaseModel)
data class CreateTaskRequest(
    val title: String,
    val description: String,
    val priority: String, // "low", "medium", "high", "urgent"
    val status: String = "pending",
    val dueDate: String, // ISO 8601 format
    val caretaker: String, // Caretaker user ID
    val patientId: String? = null
)

data class UpdateTaskRequest(
    val title: String? = null,
    val description: String? = null,
    val priority: String? = null,
    val status: String? = null,
    val dueDate: String? = null,
    val patientId: String? = null
)

data class UpdateTaskStatusRequest(
    val status: String // "pending", "in_progress", "completed", "cancelled"
)

// Simple Task data class (without BaseModel inheritance for cleaner structure)
data class TaskResponse(
    @SerializedName("_id")
    val id: String,
    val title: String,
    val description: String,
    val priority: String,
    val status: String,
    @SerializedName("due_date")
    val dueDate: String?,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    val caretaker: String,
    val patientId: String? = null
)

// Wrapper response for single task operations - FIXED VERSION
data class TaskApiResponse(
    val task: TaskResponse? = null,
    val data: TaskResponse? = null // Some APIs return data instead of task
) : BaseModel() {
    // Use a computed property instead of a function to avoid JVM signature clash
    val actualTask: TaskResponse?
        get() = task ?: data
}

// Wrapper response for task list operations - FIXED VERSION
data class TaskListApiResponse(
    val tasks: List<TaskResponse>? = null,
    val data: List<TaskResponse>? = null, // Some APIs return data instead of tasks
    val totalCount: Int = 0,
    val currentPage: Int = 1,
    val totalPages: Int = 1,
    val hasNextPage: Boolean = false,
    val hasPrevPage: Boolean = false
) : BaseModel() {
    // Use a computed property instead of a function
    val actualTasks: List<TaskResponse>
        get() = tasks ?: data ?: emptyList()
}

// For operations that just return success/failure
class TaskOperationResponse : BaseModel()

// Extension function for TaskResponse copy
fun TaskResponse.copy(
    id: String = this.id,
    title: String = this.title,
    description: String = this.description,
    priority: String = this.priority,
    status: String = this.status,
    dueDate: String? = this.dueDate,
    createdAt: String = this.createdAt,
    updatedAt: String = this.updatedAt,
    caretaker: String = this.caretaker,
    patientId: String? = this.patientId
) = TaskResponse(id, title, description, priority, status, dueDate, createdAt, updatedAt, caretaker, patientId)

// Extension function to convert CreateTaskRequest to UpdateTaskRequest
fun CreateTaskRequest.toUpdateRequest() = UpdateTaskRequest(
    title = this.title,
    description = this.description,
    priority = this.priority,
    status = this.status,
    dueDate = this.dueDate,
    patientId = this.patientId
)