package deakin.gopher.guardian.model

class MedicalDiagnostic {
    val patientId: String
    val current: Boolean
    var name: String? = null
    var patientTemp: String? = null
    var bloodPressure: String? = null
    var glucoseLevel: String? = null
    var oxygenSaturation: String? = null
    var pulseRate: String? = null
    var respirationRate: String? = null
    var bloodFatLevel: String? = null

    constructor(
        patientId: String,
        name: String?,
        bloodPressure: String?,
        patientTemp: String?,
        glucoseLevel: String?,
        oxygenSaturation: String?,
        pulseRate: String?,
        respirationRate: String?,
        bloodFatLevel: String?,
        isCurrent: Boolean
    ) {
        this.patientId = patientId
        this.name = name
        this.patientTemp = patientTemp
        this.bloodPressure = bloodPressure
        this.glucoseLevel = glucoseLevel
        this.oxygenSaturation = oxygenSaturation
        this.pulseRate = pulseRate
        this.respirationRate = respirationRate
        this.bloodFatLevel = bloodFatLevel
        current = isCurrent
    }

    constructor(patientId: String, isCurrent: Boolean) {
        this.patientId = patientId
        current = isCurrent
    }

    override fun toString(): String {
        return ("Medical_diagnostic{"
                + "patient_id='"
                + patientId
                + '\''
                + ", name='"
                + name
                + '\''
                + ", patient_temp='"
                + patientTemp
                + '\''
                + ", blood_pressure='"
                + bloodPressure
                + '\''
                + ", glicose_level='"
                + glucoseLevel
                + '\''
                + ", oxygen_saturation='"
                + oxygenSaturation
                + '\''
                + ", pulse_rate='"
                + pulseRate
                + '\''
                + ", respiration_rate='"
                + respirationRate
                + '\''
                + ", bloodfast_level='"
                + bloodFatLevel
                + '\''
                + ", isCurrent="
                + current
                + '}')
    }
}