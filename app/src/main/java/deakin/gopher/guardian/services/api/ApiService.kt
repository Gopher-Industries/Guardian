package deakin.gopher.guardian.services.api

import deakin.gopher.guardian.model.register.RegisterRequest
import deakin.gopher.guardian.model.register.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("users/register")
    fun register(
        @Body request: RegisterRequest,
    ): Call<User>
}
