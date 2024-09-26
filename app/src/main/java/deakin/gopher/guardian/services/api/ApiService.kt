package deakin.gopher.guardian.services.api

import com.google.gson.JsonElement
import deakin.gopher.guardian.api.nurse.model.LoginResponse
import deakin.gopher.guardian.api.nurse.model.NurseRegisterResponse
import deakin.gopher.guardian.api.nurse.param.LoginParam
import deakin.gopher.guardian.api.nurse.param.NurseRegisterParam
import deakin.gopher.guardian.model.register.RegisterRequest
import deakin.gopher.guardian.model.register.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("nurse/register")
    fun register(
        @Body nurseRegisterParam: NurseRegisterParam,
    ): Call<NurseRegisterResponse>

    @POST("nurse/login")
    fun login(
        @Body loginParam: LoginParam,
    ): Call<LoginResponse>

    @GET("nurse/patients")
    fun getPatients(
    ): Call<JsonElement>

    @GET("nurse/profile")
    fun getProfile(
    ): Call<JsonElement>

    @GET("nurse/reports")
    fun getDailyReports(
    ): Call<JsonElement>

}
