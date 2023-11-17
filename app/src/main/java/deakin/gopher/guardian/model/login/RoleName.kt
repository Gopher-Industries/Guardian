package deakin.gopher.guardian.model.login

import deakin.gopher.guardian.R

sealed class RoleName(val name: String) {
    data object Caretaker : RoleName(R.string.caretaker_role_name.toString())
    data object Admin : RoleName(R.string.company_admin_role_name.toString())
    data object Nurse : RoleName(R.string.nurse_role_name.toString())
}