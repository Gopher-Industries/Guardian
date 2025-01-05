package deakin.gopher.guardian.services.api

import deakin.gopher.guardian.model.BaseModel
import deakin.gopher.guardian.model.register.AuthResponse
import deakin.gopher.guardian.model.register.RegisterRequest
<<<<<<< HEAD
import deakin.gopher.guardian.communication.Message
import deakin.gopher.guardian.model.NextOfKin
import deakin.gopher.guardian.model.GP
import deakin.gopher.guardian.model.Patient
import deakin.gopher.guardian.model.MedicalDiagnostic
import deakin.gopher.guardian.model.patientdata.healthdata.PatientHealthData
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.ResponseBody
=======
>>>>>>> 4a7fb6e1ccd9a05f8a3a7f255dcb3c26923860d4
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
<<<<<<< HEAD
import retrofit2.http.DELETE
import retrofit2.http.Path
import retrofit2.http.Url
import java.util.concurrent.TimeUnit
=======
>>>>>>> 4a7fb6e1ccd9a05f8a3a7f255dcb3c26923860d4

interface ApiService {
    // Authentication endpoints
    @POST("auth/register")
    fun register(@Body request: RegisterRequest): Call<AuthResponse>

    @FormUrlEncoded
    @POST("auth/login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String,
    ): Call<AuthResponse>

    @FormUrlEncoded
    @POST("auth/send-pin")
    fun sendPin(@Field("email") email: String): Call<BaseModel>

    @FormUrlEncoded
    @POST("auth/verify-pin")
    fun verifyPin(@Field("email") email: String, @Field("otp") pin: String): Call<BaseModel>

    @FormUrlEncoded
    @POST("auth/reset-password-request")
<<<<<<< HEAD
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
        private const val BASE_URL = "http://localhost:3000/"

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

=======
    fun requestPasswordReset(
        @Field("email") email: String,
    ): Call<BaseModel>
}
>>>>>>> 4a7fb6e1ccd9a05f8a3a7f255dcb3c26923860d4
