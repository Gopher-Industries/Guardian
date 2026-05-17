package deakin.gopher.guardian.model

data class CreatePatientLogRequest(
    val patient: String,
    val title: String,
    val description: String
)