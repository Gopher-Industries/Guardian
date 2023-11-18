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
    var current: Boolean? = null
) {
    constructor(patient_id: String, isCurrent: Boolean) : this()  {
        patientId = patient_id
        current = isCurrent


    }
}

