package deakin.gopher.guardian.model.login

data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String,
    val confirmPassword: String,
)
