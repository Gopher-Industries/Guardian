package deakin.gopher.guardian.model

import com.google.gson.annotations.SerializedName

// User model
data class User(
    @SerializedName("id") val id: String,
    @SerializedName("email") val email: String,
    @SerializedName("fullname") val name: String,
    @SerializedName("role") val roleName: String
)

// Authentication response model
data class AuthResponse(
    @SerializedName("user") val user: User,
    @SerializedName("token") val token: String,
    @SerializedName("message") val message: String? = null // Optional message field
)

// Registration request model
data class RegisterRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("fullname") val name: String,
    @SerializedName("role") val role: String
)


