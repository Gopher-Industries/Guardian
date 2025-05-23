package deakin.gopher.guardian.view.general

import deakin.gopher.guardian.model.Task
import retrofit2.Response
import retrofit2.http.*

interface TaskApiService {
    @POST("tasks")
    suspend fun createTask(@Body task: Task): Response<Task>

    @GET("tasks")
    suspend fun getAllTasks(): Response<List<Task>>

    @DELETE("tasks/{id}")
    suspend fun deleteTask(@Path("id") id: String): Response<Unit>

}
