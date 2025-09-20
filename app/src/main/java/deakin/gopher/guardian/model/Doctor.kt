package deakin.gopher.guardian.model

data class Doctor(
    val id: String,
    val fullname: String,
    val specialty: String? = null,
    val photoUrl: String? = null,
)
