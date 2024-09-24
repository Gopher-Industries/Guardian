package deakin.gopher.guardian.api.nurse.param

import com.google.gson.annotations.SerializedName


data class NurseRegisterParam (

  @SerializedName("name"     ) var name     : String? = null,
  @SerializedName("email"    ) var email    : String? = null,
  @SerializedName("password" ) var password : String? = null

)