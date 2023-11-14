package com.example.tg_patient_profile.model

class MedicalDiagnostic {
    val patient_id: String
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
        patient_id: String,
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
        this.patient_id = patient_id
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

    constructor(patient_id: String, isCurrent: Boolean) {
        this.patient_id = patient_id
        current = isCurrent
    }

    override fun toString(): String {
        return ("Medical_diagnostic{"
                + "patient_id='"
                + patient_id
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