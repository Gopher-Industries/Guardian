package deakin.gopher.guardian.model.login

data class Password(val password: String) {

    fun isValid(): Boolean {
        val minLength = 8
        val maxLength = 16

        if (password.length !in minLength..maxLength) return false

        val hasUppercase = password.any { it.isUpperCase() }
        val hasLowercase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        val allowedSpecialChars = setOf('!', '@', '#', '$', '%', '^', '&', '*')
        val hasSpecialChar = password.any { it in allowedSpecialChars }

        return hasUppercase && hasLowercase && hasDigit && hasSpecialChar
    }

    fun confirmWith(other: String): Boolean {
        return password == other
    }
}
