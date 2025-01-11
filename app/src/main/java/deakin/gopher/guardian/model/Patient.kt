package deakin.gopher.guardian.model

enum class PatientStatus {
    REQUIRES_ASSISTANCE,
    NOT_EXAMINED,
    NO_ASSISTANCE_REQUIRED,
}

class Patient(
    var patientId: String? = null,
    var address: String? = null,
    var dob: String? = null,
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
    var lastExaminedTimestamp: Long = 0,
    var needsAssistance: Boolean = true,
    var status: PatientStatus = PatientStatus.REQUIRES_ASSISTANCE,
    var isArchived: Boolean = false,
    var healthData: Map<String, Any> = emptyMap(),
    var taskHistory: List<String> = emptyList(),
    var carePlanProgress: Map<String, Any> = emptyMap()
) {
    constructor(
        dateOfBirth: String,
        firstName: String,
        middleName: String,
        lastName: String,
        medicareNo: String,
        westernAffairsNo: String,
        o: String,
        o1: String,
        o2: String,
        o3: String
    ) : this() {
        this.dob = dateOfBirth
        this.firstName = firstName
        this.middleName = middleName
        this.lastName = lastName
        this.medicareNo = medicareNo
        this.westernAffairsNo = westernAffairsNo
        this.nokId1 = o
        this.nokId2 = o1
        this.gpId1 = o2
        this.gpId2 = o3

    }


    fun archivePatient() {
        isArchived = true
    }

    fun unarchivePatient() {
        isArchived = false
    }

    private fun getIsArchived(): String {
        return if (isArchived) "True" else "False"
    }

    fun examinePatient() {
        if (status == PatientStatus.REQUIRES_ASSISTANCE) {
            status = PatientStatus.NO_ASSISTANCE_REQUIRED
            needsAssistance = false
        }
    }

    override fun toString(): String {
        return (
                "Patient{" +
                        "address='" + address + '\'' +
                        ", dob='" + dob + '\'' +
                        ", patient_name='" + patientName + '\'' +
                        ", phone='" + phone + '\'' +
                        ", photo='" + photo + '\'' +
                        ", underCare='" + underCare + '\'' +
                        ", first_name='" + firstName + '\'' +
                        ", middle_name='" + middleName + '\'' +
                        ", last_name='" + lastName + '\'' +
                        ", medicareNo='" + medicareNo + '\'' +
                        ", westernAffairNo='" + westernAffairsNo + '\'' +
                        ", isArchived='" + getIsArchived() + '\'' +
                        '}'
                )
    }
}
