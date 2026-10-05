package deakin.gopher.guardian.model

data class DoctorPatientsResponse(
    val doctor: DoctorInfo?,
    val patients: List<Patient> = emptyList(),
    val pagination: DoctorPatientPagination?,
)

data class DoctorInfo(
    val _id: String?,
    val fullname: String?,
)

data class DoctorPatientPagination(
    val page: Int?,
    val limit: Int?,
    val total: Int?,
    val pages: Int?,
)
