package deakin.gopher.guardian.model

enum class PatientStatus {
    REQUIRES_ASSISTANCE,
    NOT_EXAMINED,
    NO_ASSISTANCE_REQUIRED,
}

class Patient {
    @JvmField
    var patientId: String? = null
    var address: String? = null

    @JvmField
    var dob: String? = null
    var patientName: String? = null
    var phone: String? = null
    var photo: String? = null
    var underCare: String? = null

    @JvmField
    var firstName: String

    @JvmField
    var middleName: String? = null

    @JvmField
    var lastName: String

    @JvmField
    var medicareNo: String? = null

    @JvmField
    var westernAffairsNo: String? = null
    var nokId1: String? = null
    var nokId2: String? = null
    var gpId1: String? = null
    var gpId2: String? = null

    @JvmField
    var lastExaminedTimestamp: Long = 0

    @JvmField
    var needsAssistance: Boolean

    @JvmField
    var status: PatientStatus

    @JvmField
    var isArchived: Boolean = false

    constructor(patientId: String?, firstName: String, lastName: String) {
        this.patientId = patientId
        this.firstName = firstName
        this.lastName = lastName
        status = PatientStatus.REQUIRES_ASSISTANCE
        needsAssistance = true
    }

    // constructor for adding a patient
    constructor(
        dob: String?,
        firstName: String,
        middleName: String?,
        lastName: String,
        medicareNo: String?,
        westernAffairsNo: String?,
        nokId1: String?,
        nokId2: String?,
        gpId1: String?,
        gpId2: String?,
    ) {
        this.dob = dob
        this.firstName = firstName
        this.middleName = middleName
        this.lastName = lastName
        this.medicareNo = medicareNo
        this.westernAffairsNo = westernAffairsNo
        this.nokId1 = nokId1
        this.nokId2 = nokId2
        this.gpId1 = gpId1
        this.gpId2 = gpId2
        status = PatientStatus.REQUIRES_ASSISTANCE
        needsAssistance = true
    }

    fun archivePatient() {
        isArchived = true
    }

    fun unarchivePatient() {
        isArchived = false
    }

    private fun getIsArchived(): String {
        return if (isArchived) {
            "True"
        } else {
            "False"
        }
    }

    fun getFirstName(): String {
        return this.firstName
    }

    fun getLastName(): String {
        return this.lastName
    }

    fun getPatientId(): String? {
        return this.patientId
    }

    fun examinePatient() {
        if (PatientStatus.REQUIRES_ASSISTANCE == status) {
            status = PatientStatus.NO_ASSISTANCE_REQUIRED
            needsAssistance = false
        }
    }

    override fun toString(): String {
        return (
            "Patient{" +
                "address='" +
                address +
                '\'' +
                ", dob='" +
                dob +
                '\'' +
                ", patient_name='" +
                patientName +
                '\'' +
                ", phone='" +
                phone +
                '\'' +
                ", photo='" +
                photo +
                '\'' +
                ", underCare='" +
                underCare +
                '\'' +
                ", first_name='" +
                firstName +
                '\'' +
                ", middle_name='" +
                middleName +
                '\'' +
                ", last_name='" +
                lastName +
                '\'' +
                ", medicareNo='" +
                medicareNo +
                '\'' +
                ", westernAffairNo='" +
                westernAffairsNo +
                '\'' +
                ", isArchived='" +
                getIsArchived() +
                '}'
        )
    }
}
