package deakin.gopher.guardian.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class BaseModel<T>(
    @SerializedName("data") val data: T?,
    @SerializedName("message") val message: String? = null,
    @SerializedName("status") val status: String? = null,
) : Serializable

open class ApiErrorResponse : Serializable {
    @SerializedName("error")
    val apiError: String? = null
}
