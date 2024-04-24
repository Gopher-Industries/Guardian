package deakin.gopher.guardian.model.login

import deakin.gopher.guardian.R

sealed class LoginAuthError(val messageId: Int) {
    data object EmailNotVerified : LoginAuthError(R.string.login_email_not_verified)
}
