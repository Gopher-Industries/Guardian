package deakin.gopher.guardian.model.login

data class EmailAddress(val emailAddress: String) {
    fun isValid(): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()
    }
}

sealed class EmailAddressError(val message: String) {
    data class Invalid(val emailAddress: String) :
        EmailAddressError("$emailAddress is not a valid email address")

    data object Empty : EmailAddressError("Email address must not be empty")
}
