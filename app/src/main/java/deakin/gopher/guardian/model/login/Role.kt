package deakin.gopher.guardian.model.login

data class Role(val roleName: RoleName)

sealed class RoleName(name: String) {
    data object Caretaker : RoleName("Caretaker")
    data object Admin : RoleName("Admin")
    data object Nurse : RoleName("Nurse")
}