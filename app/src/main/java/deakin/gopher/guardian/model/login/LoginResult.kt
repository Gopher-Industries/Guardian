package deakin.gopher.guardian.model.login

sealed class LoginResult(val message: String) {
    data class Success(val role: Role) : LoginResult("Login successful")
    data class Failure(val error: LoginError) : LoginResult("Login failed")

}

sealed class LoginError(val error: String) {
    data object EmailInvalidError : LoginError(EmailAddressError.Invalid.message)
    data object PasswordValidationError : LoginError(PasswordError.TooShort.message)
    data object AuthenticationError : LoginError("Authentication failed")
}


