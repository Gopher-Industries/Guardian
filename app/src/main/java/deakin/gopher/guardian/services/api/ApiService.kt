package deakin.gopher.guardian.services.api

import deakin.gopher.guardian.model.AddPatientActivityResponse
import deakin.gopher.guardian.model.AddPatientResponse
import deakin.gopher.guardian.model.BaseModel
import deakin.gopher.guardian.model.Patient
import deakin.gopher.guardian.model.PatientActivity
import deakin.gopher.guardian.model.register.AuthResponse
import deakin.gopher.guardian.model.register.RegisterRequest
import deakin.gopher.guardian.model.calendar.CreateTaskRequest
import deakin.gopher.guardian.model.calendar.TaskApiResponse
import deakin.gopher.guardian.model.calendar.TaskListApiResponse
import deakin.gopher.guardian.model.calendar.TaskResponse

import deakin.gopher.guardian.model.calendar.UpdateTaskRequest
import deakin.gopher.guardian.model.calendar.UpdateTaskStatusRequest
import deakin.gopher.guardian.model.logbook.CreateLogRequest
import deakin.gopher.guardian.model.logbook.CreateLogResponse
import deakin.gopher.guardian.model.logbook.DeleteLogResponse
import deakin.gopher.guardian.model.logbook.LogListResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // Your existing endpoints...
    @POST("auth/register")
    fun register(
        @Body request: RegisterRequest,
    ): Call<AuthResponse>

    @FormUrlEncoded
    @POST("auth/login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String,
    ): Call<AuthResponse>

    @FormUrlEncoded
    @POST("auth/send-pin")
    fun sendPin(
        @Field("email") email: String,
    ): Call<BaseModel>

    @FormUrlEncoded
    @POST("auth/verify-pin")
    fun verifyPin(
        @Field("email") email: String,
        @Field("otp") pin: String,
    ): Call<BaseModel>

    @FormUrlEncoded
    @POST("auth/reset-password-request")
    fun requestPasswordReset(
        @Field("email") email: String,
    ): Call<BaseModel>

    @GET("patients/assigned-patients")
    suspend fun getAssignedPatients(
        @Header("Authorization") token: String,
    ): Response<List<Patient>>

    @Multipart
    @POST("patients/add")
    suspend fun addPatient(
        @Header("Authorization") token: String,
        @Part("fullname") name: RequestBody,
        @Part("dateOfBirth") dob: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part photo: MultipartBody.Part?,
    ): Response<AddPatientResponse>

    @FormUrlEncoded
    @POST("patients/entryreport")
    suspend fun logPatientActivity(
        @Header("Authorization") token: String,
        @Field("patientId") patientId: String,
        @Field("activityType") activityType: String,
        @Field("timestamp") timestamp: String,
        @Field("comment") comment: String,
    ): Response<AddPatientActivityResponse>

    @GET("patients/activities")
    suspend fun getPatientActivities(
        @Header("Authorization") token: String,
        @Query("patientId") patientId: String,
    ): Response<List<PatientActivity>>

    @DELETE("patients/{id}")
    suspend fun deletePatient(
        @Header("Authorization") token: String,
        @Path("id") patientId: String,
    ): Response<BaseModel>

    @POST("caretaker/tasks")
    suspend fun createTask(
        @Header("Authorization") token: String,
        @Body request: CreateTaskRequest
    ): Response<TaskApiResponse>

    @GET("caretaker/tasks")
    suspend fun getTasks(
        @Header("Authorization") token: String,
        @Query("caretakerId") caretakerId: String,
        @Query("filter") filter: String? = null,
        @Query("status") status: String? = null,
        @Query("dueDate") dueDate: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10,
        @Query("sort") sort: String? = null
    ): Response<TaskListApiResponse>

    @PUT("caretaker/tasks/{taskId}")
    suspend fun updateTask(
        @Header("Authorization") token: String,
        @Path("taskId") taskId: String,
        @Body request: UpdateTaskRequest
    ): Response<TaskApiResponse>

    @PATCH("caretaker/tasks/{taskId}/status")
    suspend fun updateTaskStatus(
        @Header("Authorization") token: String,
        @Path("taskId") taskId: String,
        @Body request: UpdateTaskStatusRequest
    ): Response<TaskApiResponse>
    @DELETE("caretaker/tasks/{taskId}")
    suspend fun deleteTask(
        @Header("Authorization") token: String,
        @Path("taskId") taskId: String
    ): Response<BaseModel>

    @POST("patient-logs")
    suspend fun createPatientLog(
        @Header("Authorization") token: String,
        @Body request: CreateLogRequest
    ): Response<CreateLogResponse>

    @GET("patient-logs")
    suspend fun getPatientLogs(
        @Header("Authorization") token: String,
        @Query("patient") patientId: String? = null,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): Response<LogListResponse>

    @DELETE("patient-logs/{logId}")
    suspend fun deletePatientLog(
        @Header("Authorization") token: String,
        @Path("logId") logId: String
    ): Response<DeleteLogResponse>

}
