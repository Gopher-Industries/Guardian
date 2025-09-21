package deakin.gopher.guardian.model

data class PrescriptionResponse(
    val prescriptions: List<Prescription>,
    val pagination: Pagination
)

data class Pagination(
    val total: Int,
    val page: Int,
    val pages: Int,
    val limit: Int
)
