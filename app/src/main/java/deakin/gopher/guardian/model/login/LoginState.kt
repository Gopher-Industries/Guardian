package deakin.gopher.guardian.model.login

data class LoginState(
    val isLoginSuccessful: Boolean = false,
    val loginError: String? = null
)
