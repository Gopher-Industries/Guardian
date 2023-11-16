package deakin.gopher.guardian.model

data class NextOfKin(
    private var firstName: String,
    private var middleName: String,
    private var lastName: String,
    private var homeAddress: String,
    private var mobilePhone: String,
    private var emailAddress: String,
    private val photo: String? = null
)