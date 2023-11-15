package deakin.gopher.guardian.model

class Patient {
    private var patientId: String? = null
    private var address: String? = null
    private var dob: String? = null
    private var patientName: String? = null
    private var phone: String? = null
    private var photo: String? = null
    private var underCare: String? = null
    private var firstName: String? = null
    private var middleName: String? = null
    private var lastName: String? = null
    private var medicareNo: String? = null
    private var westwenAffairesNo: String? = null
    private var nokId1: String? = null
    private var nokId2: String? = null
    private var gpId1: String? = null
    private var gpId2: String? = null

    constructor(patientId: String?, firstName: String, lastName: String) {
        this.patientId = patientId
        this.firstName = firstName
        this.lastName = lastName
    }

    // constructor for adding a patient
    constructor(
        dob: String?,
        firstName: String?,
        middleName: String?,
        lastName: String?,
        medicareNo: String?,
        westwenAffairesNo: String?,
        nokId1: String?,
        nokId2: String?,
        gpId1: String?,
        gpId2: String?
    ) {
        this.dob = dob
        this.firstName = firstName
        this.middleName = middleName
        this.lastName = lastName
        this.medicareNo = medicareNo
        this.westwenAffairesNo = westwenAffairesNo
        this.nokId1 = nokId1
        this.nokId2 = nokId2
        this.gpId1 = gpId1
        this.gpId2 = gpId2
    }

    fun getPatientId(): String? {
        return patientId
    }

    fun getFirstName(): String? {
        return firstName
    }

    fun setFirstName(firstName: String?) {
        this.firstName = firstName
    }

    fun getMiddleName(): String? {
        return middleName
    }

    fun setMiddleName(middleName: String?) {
        this.middleName = middleName
    }

    fun getLastName(): String? {
        return lastName
    }

    fun setLastName(lastName: String?) {
        this.lastName = lastName
    }

    fun setDob(dob: String?) {
        this.dob = dob
    }

    fun setMedicareNo(medicareNo: String?) {
        this.medicareNo = medicareNo
    }

    fun setWestwenAffairesNo(westwenAffairesNo: String?) {
        this.westwenAffairesNo = westwenAffairesNo
    }

    fun setNokId1(nokId1: String?) {
        this.nokId1 = nokId1
    }

    fun setNokId2(nokId2: String?) {
        this.nokId2 = nokId2
    }

    fun setGpId1(gpId1: String?) {
        this.gpId1 = gpId1
    }

    fun setGpId2(gpId2: String?) {
        this.gpId2 = gpId2
    }

    override fun toString(): String {
        return ("Patient{"
                + "address='"
                + address
                + '\''
                + ", dob='"
                + dob
                + '\''
                + ", patient_name='"
                + patientName
                + '\''
                + ", phone='"
                + phone
                + '\''
                + ", photo='"
                + photo
                + '\''
                + ", underCare='"
                + underCare
                + '\''
                + ", first_name='"
                + firstName
                + '\''
                + ", middle_name='"
                + middleName
                + '\''
                + ", last_name='"
                + lastName
                + '\''
                + ", medicareNo='"
                + medicareNo
                + '\''
                + ", westwenAffairesNo='"
                + westwenAffairesNo
                + '\''
                + ", nok_id1='"
                + nokId1
                + '\''
                + ", nok_id2='"
                + nokId2
                + '\''
                + ", gp_id1='"
                + gpId1
                + '\''
                + ", gp_id2='"
                + gpId2
                + '\''
                + '}')
    }
}