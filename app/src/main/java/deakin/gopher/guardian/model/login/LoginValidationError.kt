package deakin.gopher.guardian.model.login

import deakin.gopher.guardian.R

sealed class LoginValidationError(val messageResoureId: Int) {
    data object EmptyEmail : LoginValidationError(R.string.validation_empty_email)
    data object InvalidEmail :
        LoginValidationError(R.string.validation_invalid_email_address)

    data object EmptyPassword : LoginValidationError(R.string.validation_empty_password)
    data object PasswordTooShort :
        LoginValidationError(R.string.validation_password_weak)
}
