package deakin.gopher.guardian.model

data class MedicalDiagnostic(
    val patientId: String,
    val name: String? = null,
    val bloodPressure: String? = null,
    val patientTemp: String? = null,
    val glucoseLevel: String? = null,
    val oxygenSaturation: String? = null,
    val pulseRate: String? = null,
    val respirationRate: String? = null,
    val bloodFatLevel: String? = null,
    val current: Boolean
)

