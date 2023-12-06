package deakin.gopher.guardian.model.login

import androidx.core.util.PatternsCompat

data class EmailAddress(val emailAddress: String) {
    fun isValid(): Boolean {
        return PatternsCompat.EMAIL_ADDRESS.matcher(emailAddress).matches()
    }
}
