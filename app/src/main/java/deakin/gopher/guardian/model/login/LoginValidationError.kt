package deakin.gopher.guardian.model.login

import deakin.gopher.guardian.R

sealed class LoginValidationError(val message: String) {
    data object EmptyEmail : LoginValidationError(R.string.validation_empty_email.toString())
    data object InvalidEmail :
        LoginValidationError(R.string.validation_invalid_email_address.toString())

    data object EmptyPassword : LoginValidationError(R.string.validation_empty_password.toString())
    data object PasswordTooShort :
        LoginValidationError(R.string.validation_password_short.toString())
}