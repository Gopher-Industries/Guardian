package deakin.gopher.guardian.model

data class MedicalDiagnostic(
    var patientId: String? = null,
    var name: String? = null,
    var bloodPressure: String? = null,
    var patientTemp: String? = null,
    var glucoseLevel: String? = null,
    var oxygenSaturation: String? = null,
    var pulseRate: String? = null,
    var respirationRate: String? = null,
    var bloodFatLevel: String? = null,
    var current: Boolean? = null,
) {
    constructor(patientId: String, current: Boolean) : this() {
        this.patientId = patientId
        this.current = current
    }
}
