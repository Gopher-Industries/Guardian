package deakin.gopher.guardian.model.login

data class Password(val password: String) {
    fun isValid(): Boolean {
        return password.length >= 6
    }
}

sealed class PasswordError(val message: String) {
    data object TooShort : PasswordError("Password must be at least 6 characters long")
    data object Empty : PasswordError("Password must not be empty")
}
