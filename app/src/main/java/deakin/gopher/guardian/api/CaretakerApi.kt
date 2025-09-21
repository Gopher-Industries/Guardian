package deakin.gopher.guardian.network


import deakin.gopher.guardian.model.login.Role.Caretaker
import retrofit2.Response
import retrofit2.http.*

interface CaretakerApi {
    @GET("caretaker/profile")
    suspend fun getProfile(
        @Query("caretakerId") caretakerId: String? = null,
        @Query("email") email: String? = null
    ): Response<Caretaker>

    @PUT("caretaker/profile")
    suspend fun updateProfile(
        @Body caretaker: deakin.gopher.guardian.model.Caretaker
    ): Response<Caretaker>
}
