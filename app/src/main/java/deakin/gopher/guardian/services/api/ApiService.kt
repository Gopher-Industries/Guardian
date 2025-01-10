package deakin.gopher.guardian.services.api

import deakin.gopher.guardian.model.BaseModel
import deakin.gopher.guardian.model.Task
import deakin.gopher.guardian.model.register.AuthResponse
import deakin.gopher.guardian.model.register.RegisterRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @POST("auth/register")
    fun register(
        @Body request: RegisterRequest,
    ): Call<BaseModel<AuthResponse>>

    @FormUrlEncoded
    @POST("auth/login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String,
    ): Call<BaseModel<AuthResponse>> // Use BaseModel with AuthResponse

    @FormUrlEncoded
    @POST("auth/reset-password-request")
    fun requestPasswordReset(
        @Field("email") email: String,
    ): Call<BaseModel<Unit>>

    @FormUrlEncoded
    @POST("auth/send-pin")
    fun sendPin(
        @Field("email") email: String,
    ): Call<BaseModel<Unit>>

    @FormUrlEncoded
    @POST("auth/verify-pin")
    fun verifyPin(
        @Field("email") email: String,
        @Field("otp") pin: String,
    ): Call<BaseModel<Unit>>

    @GET("tasks")
    fun getAllTasks(): Call<BaseModel<List<Task>>>

    @GET("tasks/{id}")
    fun getTaskById(
        @Path("id") taskId: String,
    ): Call<BaseModel<Task>>

    @POST("tasks")
    fun createTask(
        @Body task: Task,
    ): Call<BaseModel<Task>>

    @PUT("tasks/{id}")
    fun updateTask(
        @Path("id") taskId: String,
        @Body task: Task,
    ): Call<BaseModel<Task>>

    @PUT("tasks/{id}/complete")
    fun updateTaskCompletion(
        @Path("id") taskId: String,
        @Body isComplete: Boolean,
    ): Call<BaseModel<Unit>>

    @DELETE("tasks/{id}")
    fun deleteTask(
        @Path("id") taskId: String,
    ): Call<BaseModel<Unit>>
}
