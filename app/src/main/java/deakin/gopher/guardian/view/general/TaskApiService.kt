package deakin.gopher.guardian.view.general

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

data class TaskRequest(
    val title: String,
    val description: String,
    val patientId: String,
    val dueDate: String,
    val assignedTo: String? = null,
)

data class TaskResponse(
    val _id: String,
    val title: String,
    val description: String,
    val patientId: String,
    val dueDate: String,
    val assignedTo: String?,
)

interface TaskApiService {
    @POST("admin/tasks")
    suspend fun createTask(
        @Header("Authorization") token: String,
        @Body task: TaskRequest,
    ): Response<TaskResponse>

    // (Optional: Add GET for fetching tasks if needed)
}
