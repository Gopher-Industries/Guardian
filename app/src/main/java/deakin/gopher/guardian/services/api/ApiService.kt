package deakin.gopher.guardian.services.api

import deakin.gopher.guardian.model.BaseModel
import deakin.gopher.guardian.model.register.AuthResponse
import deakin.gopher.guardian.model.register.RegisterRequest
import deakin.gopher.guardian.communication.Message
import deakin.gopher.guardian.model.NextOfKin
import deakin.gopher.guardian.model.GP
import deakin.gopher.guardian.model.Patient
import deakin.gopher.guardian.model.MedicalDiagnostic
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.DELETE
import retrofit2.http.Path
import retrofit2.http.Url

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

    // DELETE endpoint to delete a patient
    @DELETE("patients/{Id}")
    fun deletePatient(@Path("Id") patientId: String): Call<ResponseBody>

    @GET
    fun getMessages(@Url url: String): Call<ResponseBody> // <-- Correct endpoint for GET request

    @POST
    fun sendMessage(@Url url: String, @Body message: Message): Call<ResponseBody> // <-- Correct endpoint for POST request

    // New API endpoints added below:
    @POST("nextofkin")
    fun createNextOfKin(@Body nextOfKin: NextOfKin): Call<Void> // <-- Add this method for NextOfKin creation

    @POST("gp")
    fun createGP(@Body gp: GP): Call<Void> // <-- Add this method for GP creation

    @POST("patients")
    fun createPatient(@Body patient: Patient): Call<Void> // <-- Add this method for Patient creation

    @POST("diagnostic")
    fun createMedicalDiagnostic(@Body diagnostic: MedicalDiagnostic): Call<Void> // <-- Add this method for Medical Diagnostic creation
}


