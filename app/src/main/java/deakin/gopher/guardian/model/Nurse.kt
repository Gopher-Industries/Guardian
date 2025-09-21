package deakin.gopher.guardian.model.user

data class Nurse(
    val _id: String,
    val fullname: String,
    val email: String,
    val assignedPatients: List<Any>?,
    val failedLoginAttempts: Int,
    val lastPasswordChange: String?,
    val created_at: String,
    val updated_at: String,
    val role: RoleDto,
)

data class RoleDto(
    val _id: String,
    val name: String,
)
