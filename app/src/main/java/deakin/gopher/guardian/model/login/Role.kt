package deakin.gopher.guardian.model.login

import deakin.gopher.guardian.R
import java.io.Serializable

sealed class Role(val name: String) : Serializable {
    data object Caretaker : Role(R.string.caretaker_role_name.toString().lowercase()) {
        private fun readResolve(): Any = Caretaker
    }

    data object Admin : Role(R.string.company_admin_role_name.toString().lowercase()) {
        private fun readResolve(): Any = Admin
    }

    data object Nurse : Role(R.string.nurse_role_name.toString().lowercase()) {
        private fun readResolve(): Any = Nurse
    }

    companion object {
        private const val CARETAKER_ROLE = "caretaker"
        private const val ADMIN_ROLE = "admin"
        private const val NURSE_ROLE = "nurse"

        fun create(name: String): Role {
            return when (name.lowercase()) {
                CARETAKER_ROLE.lowercase() -> Caretaker
                ADMIN_ROLE.lowercase() -> Admin
                NURSE_ROLE.lowercase() -> Nurse
                else -> throw IllegalArgumentException("Unknown role: $name")
            }
        }
    }
}
