package deakin.gopher.guardian.model.register

import com.google.gson.annotations.SerializedName
import deakin.gopher.guardian.model.BaseModel
import deakin.gopher.guardian.model.login.Role
import java.io.Serializable

data class User(
    @SerializedName(value = "id", alternate = ["_id"]) val id: String = "",
    @SerializedName(value = "fullname", alternate = ["fullName"]) val name: String = "",
    @SerializedName("email") val email: String = "",
    @SerializedName("role") val roleName: String? = null,
    @SerializedName("photoUrl") val photoUrl: String? = null,
    @SerializedName("organization") val organization: String? = null,
) : Serializable {
    val role: Role
        get() = Role.create(roleName ?: "")
}

class UserDeserializer : com.google.gson.JsonDeserializer<User?> {
    override fun deserialize(
        json: com.google.gson.JsonElement?,
        typeOfT: java.lang.reflect.Type?,
        context: com.google.gson.JsonDeserializationContext?,
    ): User? {
        if (json == null || json.isJsonNull) return null
        return if (json.isJsonPrimitive) {
            User(id = json.asString)
        } else if (json.isJsonObject) {
            val obj = json.asJsonObject
            val id = obj.get("id")?.asString ?: obj.get("_id")?.asString ?: ""
            val name = obj.get("fullname")?.asString ?: obj.get("fullName")?.asString ?: obj.get("name")?.asString ?: ""
            val email = obj.get("email")?.asString ?: ""
            val roleName = if (obj.get("role")?.isJsonObject == true) {
                obj.getAsJsonObject("role")?.get("name")?.asString
            } else {
                obj.get("role")?.asString
            }
            val photoUrl = obj.get("photoUrl")?.asString
            val organization = obj.get("organization")?.asString
            User(id = id, name = name, email = email, roleName = roleName, photoUrl = photoUrl, organization = organization)
        } else {
            null
        }
    }
}

data class NurseListResponse(
    @SerializedName("nurses") val nurses: List<NurseListItem>,
)

data class NurseListItem(
    @SerializedName("_id") val id: String,
    @SerializedName("fullname") val fullName: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("photoUrl") val photoUrl: String? = null,
    @SerializedName("role") val role: NurseRole?,
) {
    fun toUser(): User {
        return User(
            id = id,
            email = email.orEmpty(),
            name = fullName.orEmpty(),
            roleName = role?.name.orEmpty(),
            photoUrl = photoUrl.orEmpty(),
            organization = null,
        )
    }
}

data class NurseRole(
    @SerializedName("_id") val id: String,
    @SerializedName("name") val name: String,
)

data class RegisterRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("fullname") val name: String,
    @SerializedName("role") val role: String,
)

data class AuthResponse(
    @SerializedName("user") val user: User,
    @SerializedName("token") val token: String,
) : BaseModel()
