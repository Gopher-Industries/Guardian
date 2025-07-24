package deakin.gopher.guardian.model.register

import com.google.gson.annotations.SerializedName
import deakin.gopher.guardian.model.BaseModel
import deakin.gopher.guardian.model.login.Role
import java.io.Serializable

data class User(
    @SerializedName("id") val id: String,
    @SerializedName("email") val email: String,
    @SerializedName("fullname") val name: String,
    @SerializedName("role") val roleName: String,
    @SerializedName("photoUrl") val photoUrl: String,
) : Serializable {
    val role: Role
        get() {
            return Role.create(roleName)
        }
}

data class RegisterRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("fullname") val fullname: String,
    @SerializedName("role") val role: String,
)

data class AuthResponse(
    @SerializedName("user") val user: User,
    @SerializedName("token") val token: String,
) : BaseModel()
