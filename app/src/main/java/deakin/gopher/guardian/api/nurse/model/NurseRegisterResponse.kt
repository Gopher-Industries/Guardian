package deakin.gopher.guardian.api.nurse.model

import com.google.gson.annotations.SerializedName


data class NurseRegisterResponse (

  @SerializedName("message" ) var message : String? = null,
  @SerializedName("nurse"   ) var nurse   : Nurse?  = Nurse()

){
  data class Nurse (

    @SerializedName("isApproved"         ) var isApproved         : Boolean?          = null,
    @SerializedName("assignedPatients"   ) var assignedPatients   : ArrayList<String> = arrayListOf(),
    @SerializedName("role"               ) var role               : String?           = null,
    @SerializedName("_id"                ) var Id                 : String?           = null,
    @SerializedName("name"               ) var name               : String?           = null,
    @SerializedName("email"              ) var email              : String?           = null,
    @SerializedName("password"           ) var password           : String?           = null,
    @SerializedName("lastPasswordChange" ) var lastPasswordChange : String?           = null,
    @SerializedName("createdAt"          ) var createdAt          : String?           = null,
    @SerializedName("updatedAt"          ) var updatedAt          : String?           = null,
    @SerializedName("__v"                ) var _v                 : Int?              = null

  )
}