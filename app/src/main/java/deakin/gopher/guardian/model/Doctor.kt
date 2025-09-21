package deakin.gopher.guardian.model

import java.io.Serializable

data class Doctor(
    var id: String? = null,
    var fullName: String? = null,
    var specialization: String? = null,
    var phone: String? = null,
    var email: String? = null,
    var hospital: String? = null,
    var assignedPatients: List<String>? = null
) : Serializable
