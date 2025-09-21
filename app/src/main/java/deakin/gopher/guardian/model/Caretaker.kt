@file:Suppress("ktlint:standard:no-wildcard-imports")

package deakin.gopher.guardian.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

//
// data class Caretaker(
//    @SerializedName("_id")
//    val id: String?,
//
//    @SerializedName("fullname")
//    var fullName: String?,
//
//    @SerializedName("email")
//    val email: String?,
//
//    @SerializedName("assignedPatients")
//    val assignedPatients: List<String>? = emptyList(),
//
//    @SerializedName("failedLoginAttempts")
//    val failedLoginAttempts: Int? = 0,
//
//    @SerializedName("lastPasswordChange")
//    val lastPasswordChange: String? = null,
//
//    @SerializedName("created_at")
//    val createdAt: String? = null,
//
//    @SerializedName("updated_at")
//    val updatedAt: String? = null,
//
//    @SerializedName("role")
//    val role: Role? = null
// ) : Serializable
//
// data class Role(
//    @SerializedName("_id")
//    val id: String?,
//
//    @SerializedName("name")
//    val name: String?
// ) : Serializable
//
data class Caretaker(
    @SerializedName("_id")
    val id: String?,
    @SerializedName("fullname")
    var fullName: String?,
    @SerializedName("address")
    var address: String? = null,
    @SerializedName("dob")
    var dob: String? = null,
    @SerializedName("phone")
    var phone: String? = null,
    @SerializedName("ward")
    var ward: String? = null,
    @SerializedName("medicareNumber")
    var medicareNumber: String? = null,
    @SerializedName("emergencyContact")
    var emergencyContact: String? = null,
    @SerializedName("email")
    var email: String?,
    @SerializedName("assignedPatients")
    val assignedPatients: List<String>? = emptyList(),
    @SerializedName("failedLoginAttempts")
    val failedLoginAttempts: Int? = 0,
    @SerializedName("lastPasswordChange")
    val lastPasswordChange: String? = null,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null,
) : Serializable
