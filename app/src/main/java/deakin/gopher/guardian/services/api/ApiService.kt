package deakin.gopher.guardian.services.api

import deakin.gopher.guardian.model.AddPatientActivityResponse
import deakin.gopher.guardian.model.AddPatientResponse
import deakin.gopher.guardian.model.BaseModel
import deakin.gopher.guardian.model.Patient
import deakin.gopher.guardian.model.PatientActivity
import deakin.gopher.guardian.model.register.AuthResponse
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
}
