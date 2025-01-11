package deakin.gopher.guardian.services.api

import deakin.gopher.guardian.communication.Message
import deakin.gopher.guardian.model.AuthResponse
import deakin.gopher.guardian.model.BaseModel
import deakin.gopher.guardian.model.Patient
import deakin.gopher.guardian.model.RegisterRequest
import deakin.gopher.guardian.model.Notification
import deakin.gopher.guardian.model.MessageRequest
import deakin.gopher.guardian.model.MessageResponse
import deakin.gopher.guardian.model.CarePlan

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // Authentication
    @POST("auth/register")
    fun register(@Body request: RegisterRequest): Call<AuthResponse>

    @FormUrlEncoded
    @POST("auth/login")
    fun login(@Field("email") email: String, @Field("password") password: String): Call<AuthResponse>

    @FormUrlEncoded
    @POST("auth/send-pin")
    fun sendPin(@Field("email") email: String): Call<BaseModel>

    @FormUrlEncoded
    @POST("auth/verify-pin")
    fun verifyPin(@Field("email") email: String, @Field("otp") pin: String): Call<BaseModel>

    @FormUrlEncoded
    @POST("auth/reset-password-request")
    fun requestPasswordReset(@Field("email") email: String): Call<BaseModel>

    @POST("auth/signin")
    suspend fun signIn(@Field("email") email: String, @Field("password") password: String): Response<Any>

    @POST("auth/signup")
    suspend fun createAccount(@Field("email") email: String, @Field("password") password: String): Response<Any>

    @POST("auth/reset-password")
    suspend fun resetPassword(@Field("email") email: String): Response<Any>

    @POST("auth/googleSignIn")
    fun googleSignIn(@Body idToken: String): Call<AuthResponse>

    // Notifications
    @GET("api/notifications")
    fun getNotifications(): Call<List<Notification>>

    @POST("api/notifications/{id}/markRead")
    fun markNotificationAsRead(@Path("id") notificationId: Int): Call<BaseModel>

    @DELETE("api/notifications/{id}")
    fun deleteNotification(@Path("id") notificationId: Int): Call<BaseModel>

    // Messaging
    @POST("api/sendMessage")
    fun sendMessage(@Body request: Message): Call<MessageResponse>

    // Patients (Existing Methods)
    @GET("patients")
    fun getPatients(): Call<List<Patient>>

    @GET("patients/search")
    fun searchPatients(@Query("query") query: String): Call<List<Patient>>

    @DELETE("patients/{id}")
    fun deletePatient(@Path("id") patientId: String): Call<ResponseBody>

    @GET("patients/{id}")
    fun getPatient(@Path("id") patientId: String): Call<Patient>

    @GET("careplans")
    fun getCarePlans(): Call<List<CarePlan>>

    @GET("api/getMessage")
    fun getMessages():Call<List<Message>>

}



