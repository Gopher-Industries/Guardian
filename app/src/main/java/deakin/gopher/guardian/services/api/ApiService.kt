package deakin.gopher.guardian.services.api

import deakin.gopher.guardian.model.BaseModel
import deakin.gopher.guardian.model.register.AuthResponse
import deakin.gopher.guardian.model.register.RegisterRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {
    @POST("api/v1/auth/register")
    fun register(
        @Body request: RegisterRequest,
    ): Call<AuthResponse>

    @FormUrlEncoded
    @POST("api/v1/auth/login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String,
    ): Call<AuthResponse>

    @FormUrlEncoded
    @POST("api/v1/auth/send-pin")
    fun sendPin(
        @Field("email") email: String,
    ): Call<BaseModel>

    @FormUrlEncoded
    @POST("api/v1/auth/verify-pin")
    fun verifyPin(
        @Field("email") email: String,
        @Field("otp") pin: String,
    ): Call<BaseModel>

    @FormUrlEncoded
    @POST("api/v1/auth/reset-password-request")
    fun requestPasswordReset(
        @Field("email") email: String,
    ): Call<BaseModel>
}
