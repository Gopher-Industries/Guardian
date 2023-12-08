package deakin.gopher.guardian.model.register

import deakin.gopher.guardian.R

sealed class RegistrationError(val messageResourceId: Int) {
    data object EmptyEmail : RegistrationError(R.string.validation_empty_email)
    data object InvalidEmail :
        RegistrationError(R.string.validation_invalid_email_address)

    data object EmptyPassword : RegistrationError(R.string.validation_empty_password)
    data object PasswordTooShort :
        RegistrationError(R.string.validation_password_weak)

    data object EmptyConfirmedPassword :
        RegistrationError(R.string.validation_empty_confirmed_password)

    data object PasswordsFailConfirmation :
        RegistrationError(R.string.validation_error_passwords_do_not_match)
}
