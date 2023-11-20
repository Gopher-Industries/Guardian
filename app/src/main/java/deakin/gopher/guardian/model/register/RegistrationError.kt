package deakin.gopher.guardian.model.register

import deakin.gopher.guardian.R

sealed class RegistrationError(val message: String) {
    data object EmptyEmail : RegistrationError(R.string.validation_empty_email.toString())
    data object InvalidEmail :
        RegistrationError(R.string.validation_invalid_email_address.toString())

    data object EmptyPassword : RegistrationError(R.string.validation_empty_password.toString())
    data object PasswordTooShort :
        RegistrationError(R.string.validation_password_short.toString())

    data object EmptyConfirmedPassword :
        RegistrationError(R.string.validation_empty_confirmed_password.toString())

    data object PasswordsFailConfirmation :
        RegistrationError(R.string.validation_error_passwords_do_not_match.toString())
}
