package deakin.gopher.guardian.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

open class BaseModel : ApiErrorResponse() {
    @SerializedName("message")
    val apiMessage: String? = null
}

open class ApiErrorResponse : Serializable {
    @SerializedName("error")
    val apiError: String? = null
}
