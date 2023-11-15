package deakin.gopher.guardian.model.login

data class Role(val roleName: RoleName) {
    fun isValid(): Boolean {
        return when (roleName) {
            is RoleName.Caretaker -> true
            is RoleName.Admin -> true
            is RoleName.Nurse -> true
        }
    }

}

sealed class RoleName(name: String) {
    data object Caretaker : RoleName("Caretaker")
    data object Admin : RoleName("Admin")
    data object Nurse : RoleName("Nurse")
}

sealed class RoleError(val message: String) {
    data object Empty : RoleError("Please select a role")
}