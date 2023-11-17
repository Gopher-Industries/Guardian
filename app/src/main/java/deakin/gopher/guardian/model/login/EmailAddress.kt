package deakin.gopher.guardian.model.login

data class EmailAddress(val emailAddress: String) {
    fun isValid(): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()
    }
}
