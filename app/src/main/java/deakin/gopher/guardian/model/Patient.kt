package deakin.gopher.guardian.model

import com.google.gson.annotations.SerializedName
import deakin.gopher.guardian.model.register.User
import java.io.Serializable
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId

data class Patient(
    @SerializedName("_id") val id: String,
    @SerializedName("fullname") val fullname: String,
    @SerializedName("photoUrl") val photoUrl: String? = null,
    @SerializedName("dateOfBirth") val dateOfBirth: String? = null,
    @SerializedName("age") val _age: Int,
    @SerializedName("gender") val gender: String,
    @SerializedName("healthConditions") val healthConditions: List<String> = emptyList(),
    @SerializedName("caretaker") val caretaker: User? = null,
    @SerializedName("assignedNurses") val assignedNurses: List<User> = emptyList(),
    @SerializedName("assignedDoctor") val assignedDoctor: User? = null,
) : Serializable {
    val age: Int
        get() {
            return calculateAge(dateOfBirth, _age)
        }
}

// Helper function to calculate age
private fun calculateAge(
    dateOfBirthString: String?,
    defaultAge: Int = 0,
): Int {
    if (dateOfBirthString.isNullOrEmpty()) {
        return defaultAge // Or throw an IllegalArgumentException, or handle as needed
    }
    return try {
        val birthInstant = Instant.parse(dateOfBirthString)
        val birthDate = birthInstant.atZone(ZoneId.systemDefault()).toLocalDate()

        val currentDate = LocalDate.now()
        Period.between(birthDate, currentDate).years
    } catch (e: Exception) {
        println("Error parsing date of birth: ${e.message}")
        defaultAge // Or throw an exception
    }
}

data class AssignNurseRequest(
    @SerializedName("nurseId") val nurseId: String,
    @SerializedName("patientId") val patientId: String,
)

data class AddPatientResponse(
    @SerializedName("patient") val patient: Patient,
) : BaseModel()

data class PatientActivity(
    @SerializedName("activityType") val activityName: String,
    @SerializedName("activityTimestamp") val timestamp: String,
    @SerializedName("nurse") val loggedBy: String,
    @SerializedName("comment") val comment: String,
)

data class AddPatientActivityResponse(
    @SerializedName("activity") val activity: PatientActivity,
) : BaseModel()

data class AdminPatientListResponse(
    @SerializedName("patients") val patients: List<Patient> = emptyList(),
)

data class PatientOverviewResponse(
    @SerializedName("patient") val patient: Patient? = null,
)

data class StaffListResponse(
    @SerializedName("staff") val staff: List<StaffMember> = emptyList(),
)

data class StaffMember(
    @SerializedName(value = "_id", alternate = ["id"]) val id: String,
    @SerializedName("fullname") val fullname: String,
    @SerializedName("email") val email: String,
    @SerializedName("photoUrl") val photoUrl: String? = null,
    @SerializedName("role") val role: StaffRole? = null,
) {
    fun displayLabel(): String {
        return if (email.isBlank()) {
            fullname
        } else {
            "$fullname ($email)"
        }
    }
}

data class StaffRole(
    @SerializedName("name") val name: String? = null,
)

data class ReassignPatientRequest(
    @SerializedName("caretakerId") val caretakerId: String,
    @SerializedName("nurseId") val nurseId: String,
    @SerializedName("doctorId") val doctorId: String,
)

data class UpdatePatientRequest(
    @SerializedName("fullName") val fullName: String,
    @SerializedName("dateOfBirth") val dateOfBirth: String,
    @SerializedName("gender") val gender: String,
)
