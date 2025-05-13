package deakin.gopher.guardian.model

import java.io.Serializable

open class BaseModel : ApiErrorResponse() {
    @SerializedName("message")
    val apiMessage: String? = null
}

annotation class SerializedName(val string: String)

open class ApiErrorResponse : Serializable {
    @SerializedName("error")
    val apiError: String? = null
}
