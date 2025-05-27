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
    @SerializedName("photoUrl") val photoUrl: String,
    @SerializedName("dateOfBirth") val dateOfBirth: String,
    @SerializedName("age") val _age: Int,
    @SerializedName("gender") val gender: String,
    @SerializedName("healthConditions") val healthConditions: List<String>,
    @SerializedName("caretaker") val caretaker: User,
    @SerializedName("assignedNurses") val assignedNurses: List<User>,
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
