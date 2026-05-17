package deakin.gopher.guardian.model

data class PatientLog(
    val _id: String,
    val patient: String,
    val title: String,
    val description: String,
    val createdBy: CreatedBy,
    val createdAt: String
)

data class CreatedBy(
    val _id: String,
    val fullname: String,
    val role: String
)