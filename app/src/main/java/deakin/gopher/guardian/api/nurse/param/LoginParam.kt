package deakin.gopher.guardian.api.nurse.param

import com.google.gson.annotations.SerializedName


data class LoginParam (

  @SerializedName("email"    ) var email    : String? = null,
  @SerializedName("password" ) var password : String? = null

)