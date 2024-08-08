package deakin.gopher.guardian.model

import deakin.gopher.guardian.R

sealed class RegistrationStatusMessage(val message: Int) {
    data object Success : RegistrationStatusMessage(R.string.registration_success)

    data object Failure : RegistrationStatusMessage(R.string.registration_failure)
}
