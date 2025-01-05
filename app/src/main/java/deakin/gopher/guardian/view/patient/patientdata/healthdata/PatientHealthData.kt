package deakin.gopher.guardian.model.patientdata.healthdata

data class PatientHealthData(
    val bloodPressure: List<Float>,
    val heartRate: List<Float>,
    val glucoseLevel: List<Float>,
    val oxygenSaturation: List<Float>,
    val respirationRate: List<Float>
)
