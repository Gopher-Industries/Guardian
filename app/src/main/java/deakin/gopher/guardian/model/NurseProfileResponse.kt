package deakin.gopher.guardian.model

data class NurseProfileResponse(
    val _id: String?,
    val fullname: String?,
    val email: String?,
    val role: NurseRole?,
    val assignedPatients: List<AssignedPatient>?,
    val approvalStatus: String?,
    val organization: NurseOrganization?
)

data class NurseRole(
    val _id: String?,
    val name: String?
)

data class AssignedPatient(
    val _id: String?,
    val fullname: String?,
    val gender: String?,
    val dateOfBirth: String?,
    val age: Int?,
    val id: String?
)

data class NurseOrganization(
    val _id: String?,
    val name: String?,
    val staffCount: Int?,
    val id: String?
)