package deakin.gopher.guardian.services.api

import com.google.gson.JsonElement
import deakin.gopher.guardian.model.AddPatientActivityResponse
import deakin.gopher.guardian.model.AddPatientResponse
import deakin.gopher.guardian.model.AdminPatientListResponse
import deakin.gopher.guardian.model.AssignNurseRequest
import deakin.gopher.guardian.model.BaseModel
import deakin.gopher.guardian.model.CreatePatientLogRequest
import deakin.gopher.guardian.model.Patient
import deakin.gopher.guardian.model.PatientActivity
import deakin.gopher.guardian.model.PatientOverviewResponse
import deakin.gopher.guardian.model.PatientLog
import deakin.gopher.guardian.model.ReassignPatientRequest
import deakin.gopher.guardian.model.UpdatePatientRequest
import deakin.gopher.guardian.model.register.AuthResponse
import deakin.gopher.guardian.model.register.NurseListResponse
import deakin.gopher.guardian.model.register.RegisterRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
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

    @GET("admin/patients")
    suspend fun getAdminPatients(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 100,
    ): Response<AdminPatientListResponse>

    @Multipart
    @POST("patients/add")
    suspend fun addPatient(
        @Header("Authorization") token: String,
        @Part("fullname") name: RequestBody,
        @Part("dateOfBirth") dob: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part photo: MultipartBody.Part?,
    ): Response<AddPatientResponse>

    @GET("nurse/all")
    suspend fun getAllNurses(
        @Header("Authorization") token: String,
        @Query("q") query: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50,
    ): Response<NurseListResponse>

    @PUT("patients/{patientId}")
    suspend fun updatePatient(
        @Header("Authorization") token: String,
        @Path("patientId") patientId: String,
        @Body request: UpdatePatientRequest,
    ): Response<BaseModel>

    @Multipart
    @PUT("patients/{patientId}")
    suspend fun updatePatientWithPhoto(
        @Header("Authorization") token: String,
        @Path("patientId") patientId: String,
        @Part("fullName") fullName: RequestBody,
        @Part("dateOfBirth") dateOfBirth: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part photo: MultipartBody.Part,
    ): Response<BaseModel>

    @POST("patients/assign-nurse")
    suspend fun assignNurseToPatient(
        @Header("Authorization") token: String,
        @Body request: AssignNurseRequest,
    ): Response<BaseModel>

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

    @GET("admin/patients/{id}/overview")
    suspend fun getPatientOverview(
        @Header("Authorization") token: String,
        @Path("id") patientId: String,
        @Query("orgId") organizationId: String? = null,
    ): Response<PatientOverviewResponse>

    @GET("caretaker")
    suspend fun getCaretakers(
        @Header("Authorization") token: String,
    ): Response<JsonElement>

    @GET("nurse/all")
    suspend fun getNurses(
        @Header("Authorization") token: String,
    ): Response<JsonElement>

    @GET("doctors")
    suspend fun getDoctors(
        @Header("Authorization") token: String,
    ): Response<JsonElement>

    @PUT("admin/patients/{id}/reassign")
    suspend fun reassignPatient(
        @Header("Authorization") token: String,
        @Path("id") patientId: String,
        @Body request: ReassignPatientRequest,
    ): Response<BaseModel>

    @DELETE("patients/{id}")
    suspend fun deletePatient(
        @Header("Authorization") token: String,
        @Path("id") patientId: String,
    ): Response<BaseModel>

    // For Patient Logs
    @GET("patient-logs/{patientId}")
    suspend fun getPatientLogs(
        @Header("Authorization") token: String,
        @Path("patientId") patientId: String,
    ): Response<List<PatientLog>>

    @POST("patient-logs")
    suspend fun createPatientLog(
        @Header("Authorization") token: String,
        @Body log: CreatePatientLogRequest,
    ): Response<PatientLog>

    @DELETE("patient-logs/{id}")
    suspend fun deletePatientLog(
        @Header("Authorization") token: String,
        @Path("id") id: String,
    ): Response<BaseModel>
}
