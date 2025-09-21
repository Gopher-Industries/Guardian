//package deakin.gopher.guardian.model.login
//
//import deakin.gopher.guardian.R
//import java.io.Serializable
//
//sealed class Role(val name: String) : Serializable {
//    data object Caretaker : Role(R.string.caretaker_role_name.toString().lowercase()) {
//        private fun readResolve(): Any = Caretaker
//    }
//
//    data object Admin : Role(R.string.company_admin_role_name.toString().lowercase()) {
//        private fun readResolve(): Any = Admin
//    }
//
//    data object Nurse : Role(R.string.nurse_role_name.toString().lowercase()) {
//        private fun readResolve(): Any = Nurse
//    }
//    data object Doctor : Role("Doctor".toString().lowercase()) {
//        private fun readResolve(): Any = Doctor
//    }
//
//
//    companion object {
//        private const val CARETAKER_ROLE = "caretaker"
//        private const val ADMIN_ROLE = "admin"
//        private const val NURSE_ROLE = "nurse"
//        private const val DOCTOR_ROLE = "doctor"
//
//        fun create(name: String): Role {
//            return when (name.lowercase()) {
//                CARETAKER_ROLE.lowercase() -> Caretaker
//                ADMIN_ROLE.lowercase() -> Admin
//                NURSE_ROLE.lowercase() -> Nurse
//                "doctor" -> Doctor
//                else -> throw IllegalArgumentException("Unknown role: $name")
//            }
//        }
//    }
//}

package deakin.gopher.guardian.model.login

import java.io.Serializable

sealed class Role(val name: String) : Serializable {

    data object Caretaker : Role("caretaker") {
        private fun readResolve(): Any = Caretaker
    }

    data object Admin : Role("admin") {
        private fun readResolve(): Any = Admin
    }

    data object Nurse : Role("nurse") {
        private fun readResolve(): Any = Nurse
    }

    data object Doctor : Role("doctor") {
        private fun readResolve(): Any = Doctor
    }

    companion object {
        private const val CARETAKER_ROLE = "caretaker"
        private const val ADMIN_ROLE = "admin"
        private const val NURSE_ROLE = "nurse"
        private const val DOCTOR_ROLE = "doctor"

        fun create(name: String): Role {
            val normalized = name.trim().lowercase()
            android.util.Log.e("RoleDebug", "Received role: '$name' -> normalized: '$normalized'")

            return when (normalized) {
                CARETAKER_ROLE -> Caretaker
                ADMIN_ROLE -> Admin
                NURSE_ROLE -> Nurse
                DOCTOR_ROLE -> Doctor
                else -> throw IllegalArgumentException("Unknown role: $name")
            }
        }
    }
}
