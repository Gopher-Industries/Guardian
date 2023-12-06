package deakin.gopher.guardian.model

data class NextOfKin(
    private var firstName: String,
    private var middleName: String,
    private var lastName: String,
    private var homeAddress: String,
    private var mobilePhone: String,
    private var emailAddress: String,
    private val photo: String? = null,
) {
    fun setHomeAddress(homeAddress: String) {
        this.homeAddress = homeAddress
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

    fun setMobilePhone(mobilePhone: String) {
        this.mobilePhone = mobilePhone
    }

    fun setEmailAddress(emailAddress: String) {
        this.emailAddress = emailAddress
    }

    override fun toString(): String {
        return (
            "NextofKin{" +
                "first_name='" +
                firstName +
                '\'' +
                ", middle_name='" +
                middleName +
                '\'' +
                ", last_name='" +
                lastName +
                '\'' +
                ", home_address='" +
                homeAddress +
                '\'' +
                ", mobile_phone='" +
                mobilePhone +
                '\'' +
                ", email_address='" +
                emailAddress +
                '\'' +
                ", photo='" +
                photo +
                '\'' +
                '}'
        )
    }
}
