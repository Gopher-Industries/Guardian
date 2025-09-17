package deakin.gopher.guardian.model.login

import deakin.gopher.guardian.R
import java.io.Serializable

sealed class RoleName(val name: String) : Serializable {
    data object Caretaker : RoleName(R.string.caretaker_role_name.toString()) {
        private fun readResolve(): Any = Caretaker
    }

    data object Admin : RoleName(R.string.company_admin_role_name.toString()) {
        private fun readResolve(): Any = Admin
    }

    data object Nurse : RoleName(R.string.nurse_role_name.toString()) {
        private fun readResolve(): Any = Nurse
    }

    data object Doctor : RoleName(R.string.doctor_role_name.toString()) {
        private fun readResolve(): Any = Doctor
    }





}
