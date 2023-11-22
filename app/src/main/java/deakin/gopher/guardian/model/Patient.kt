package deakin.gopher.guardian.model;

data class Patient(
    private var patientId: String? = null,
    private var address: String? = null,
    private var dateOfBirth: String? = null,
    private var patientName: String? = null,
    private var phone: String? = null,
    private var photo: String? = null,
    private var underCare: String? = null,
    private var firstName: String? = null,
    private var middleName: String? = null,
    private var lastName: String? = null,
    private var medicareNo: String? = null,
    private var westwenAffairesNo: String? = null,
    private var nokId1: String? = null,
    private var nokId2: String? = null,
    private var gpId1: String? = null,
    private var gpId2: String? = null,
) {

    constructor(Key: String, firstname: String, lastname: String) : this() {
        firstName = firstname
        lastName = lastname
    }

    constructor(
        dateOfBirth: String,
        firstName: String,
        middleName: String,
        lastName: String,
        medicareNo: String,
        westernAffairsNo: String
    ) : this() {
        this.dateOfBirth = dateOfBirth
        this.firstName = firstName
        this.middleName = middleName
        this.lastName = lastName
        this.medicareNo = medicareNo
        this.westwenAffairesNo = westernAffairsNo
    }

    fun getPatientId() = "$patientId"

    fun getFirstName() = "$firstName"

    fun getMiddleName() = "$middleName"

    fun getLastName() = "$lastName"


    fun setNokId1(nokId1: String) {
        this.nokId1 = nokId1
    }

    fun setNokId2(nokId2: String) {
        this.nokId2 = nokId2
    }

    fun setGpId1(gpId1: String) {
        this.gpId1 = gpId1
    }

    fun setGpId2(gpId2: String) {
        this.gpId2 = gpId2
    }

    fun setFirstName(firstName: String) {
        this.firstName = firstName
    }

    fun setMiddleName(middleName: String) {
        this.middleName = middleName
    }

    fun setLastName(lastName: String) {
        this.lastName = lastName
    }

    fun setMedicareNo(medicareNo: String) {
        this.medicareNo = medicareNo
    }

    fun setDob(dateOfBirth: String) {
        this.dateOfBirth = dateOfBirth
    }

    fun setWestwenAffairesNo(westwenAffairesNo: String) {
        this.westwenAffairesNo = westwenAffairesNo
    }
}