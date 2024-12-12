package deakin.gopher.guardian.model

enum class PatientStatus {
    REQUIRES_ASSISTANCE,
    NOT_EXAMINED,
    NO_ASSISTANCE_REQUIRED,
}

data class Patient(
    var patientId: String = "",
    var address: String = "",
    var dob: String = "",
    var patientName: String? = null,
    var phone: String? = null,
    var photo: String? = null,
    var underCare: String? = null,
    var firstName: String = "",
    var middleName: String? = null,
    var lastName: String = "",
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
    // Secondary constructor for simplified initialization
    constructor(
        patientId: String,
        firstName: String,
        lastName: String,
        isArchived: Boolean
    ) : this(
        patientId = patientId,
        firstName = firstName,
        lastName = lastName,
        isArchived = isArchived,
        needsAssistance = true,
        status = PatientStatus.REQUIRES_ASSISTANCE
    )

    fun archivePatient() {
        isArchived = true
    }

    fun unarchivePatient() {
        isArchived = false
    }

    fun examinePatient() {
        if (status == PatientStatus.REQUIRES_ASSISTANCE) {
            status = PatientStatus.NO_ASSISTANCE_REQUIRED
            needsAssistance = false
        }
    }

    override fun toString(): String {
        return "Patient(patientId='$patientId', firstName='$firstName', lastName='$lastName', isArchived=$isArchived)"
    }
}


