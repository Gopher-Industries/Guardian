package deakin.gopher.guardian.services.api

import deakin.gopher.guardian.model.BaseModel
import deakin.gopher.guardian.model.register.AuthResponse
import deakin.gopher.guardian.model.register.RegisterRequest
import deakin.gopher.guardian.communication.Message
import deakin.gopher.guardian.model.NextOfKin
import deakin.gopher.guardian.model.GP
import deakin.gopher.guardian.model.Patient
import deakin.gopher.guardian.model.MedicalDiagnostic
import deakin.gopher.guardian.model.patientdata.healthdata.PatientHealthData
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

import java.util.concurrent.TimeUnit

interface ApiService {
    // Authentication endpoints
    @POST("auth/register")
    fun register(@Body request: RegisterRequest): Call<AuthResponse>

    @FormUrlEncoded
    @POST("auth/login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<AuthResponse>

    @FormUrlEncoded
    @POST("auth/send-pin")
    fun sendPin(@Field("email") email: String): Call<BaseModel>

    @FormUrlEncoded
    @POST("auth/verify-pin")
    fun verifyPin(
        @Field("email") email: String,
        @Field("otp") pin: String
    ): Call<BaseModel>

    @FormUrlEncoded
    @POST("auth/reset-password-request")
    fun requestPasswordReset(@Field("email") email: String): Call<BaseModel>

    // CRUD operations for patients
    @DELETE("patients/{Id}")
    fun deletePatient(@Path("Id") patientId: String): Call<ResponseBody>

    @POST("patients")
    fun createPatient(@Body patient: Patient): Call<Void>

    // Fetch health data for a specific patient
    @GET("patients/{id}/healthdata")
    fun getPatientHealthData(@Path("id") patientId: String): Call<PatientHealthData>

    // Next of Kin
    @POST("nextofkin")
    fun createNextOfKin(@Body nextOfKin: NextOfKin): Call<Void>

    // GP
    @POST("gp")
    fun createGP(@Body gp: GP): Call<Void>

    // Medical Diagnostic
    @POST("diagnostic")
    fun createMedicalDiagnostic(@Body diagnostic: MedicalDiagnostic): Call<Void>

    // Messaging endpoints
    @GET
    fun getMessages(@Url url: String): Call<ResponseBody>

    @POST
    fun sendMessage(@Url url: String, @Body message: Message): Call<ResponseBody>

    companion object {
        private const val BASE_URL = "http://10.0.2.2:3000/api/v1"

        fun create(): ApiService {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }
}
