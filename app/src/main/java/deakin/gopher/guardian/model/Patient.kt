package deakin.gopher.guardian.model

enum class PatientStatus {
    REQUIRES_ASSISTANCE,
    NOT_EXAMINED,
    NO_ASSISTANCE_REQUIRED,
}

data class Patient(
    var patientId: String? = null,
    var address: String? = null,
    var dob: String? = null,
    var patientName: String? = null,
    var phone: String? = null,
    var photo: String? = null,
    var underCare: String? = null,
    var firstName: String,
    var middleName: String? = null,
    var lastName: String,
    var medicareNo: String? = null,
    var westernAffairsNo: String? = null,
    var nokId1: String? = null,
    var nokId2: String? = null,
    var gpId1: String? = null,
    var gpId2: String? = null,
    var lastExaminedTimestamp: Long = 0L,
    var needsAssistance: Boolean = true,
    var status: PatientStatus = PatientStatus.REQUIRES_ASSISTANCE,
    var isArchived: Boolean = false,
    var healthData: Map<String, Any> = emptyMap(),
    var taskHistory: List<String> = emptyList(),
    var carePlanProgress: Map<String, Any> = emptyMap(),
    var id: String? = null
) {
    // Archive or unarchive a patient
    fun archivePatient() {
        isArchived = true
    }

    fun unarchivePatient() {
        isArchived = false
    }

    // Examine a patient and update their status
    fun examinePatient() {
        if (status == PatientStatus.REQUIRES_ASSISTANCE) {
            status = PatientStatus.NO_ASSISTANCE_REQUIRED
            needsAssistance = false
        }
    }

    override fun toString(): String {
        return "Patient(" +
                "patientId='$patientId', " +
                "address='$address', " +
                "dob='$dob', " +
                "patientName='$patientName', " +
                "phone='$phone', " +
                "photo='$photo', " +
                "underCare='$underCare', " +
                "firstName='$firstName', " +
                "middleName='$middleName', " +
                "lastName='$lastName', " +
                "medicareNo='$medicareNo', " +
                "westernAffairsNo='$westernAffairsNo', " +
                "isArchived=$isArchived" +
                ")"
    }
}

