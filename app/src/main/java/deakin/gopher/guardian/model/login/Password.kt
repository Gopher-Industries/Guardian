package deakin.gopher.guardian.model.login

data class Password(val password: String) {
    fun isValid(): Boolean {
        if (password.isEmpty()) {
            return false // Password is empty, not valid
        }

        val minLength = 12
        val hasUppercase = password.any { it.isUpperCase() }
        val hasLowercase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }

        val allowedSpecialChars = setOf('!', '@', '#', '$', '%', '^', '&', '*')
        val hasSpecialChar = password.any { it in allowedSpecialChars }

        return password.length >= minLength &&
            hasUppercase &&
            hasLowercase &&
            hasDigit &&
            hasSpecialChar
    }

    fun confirmWith(other: String): Boolean {
        return password == other
    }
}
