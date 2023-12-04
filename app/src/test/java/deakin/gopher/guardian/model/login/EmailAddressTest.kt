package deakin.gopher.guardian.model.login

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EmailAddressTest {
    @Test
    fun isValid_WithNoAtSymbol_ReturnsFalse() {
        val emailAddress = EmailAddress("BadAddress-NoAtSymbol.com")
        assertFalse(emailAddress.isValid())
    }

    @Test
    fun isValid_WithNoDomain_ReturnsFalse() {
        val emailAddress = EmailAddress("BadAddress@gmail")
        assertFalse(emailAddress.isValid())
    }

    @Test
    fun isValid_WithNoProvider_ReturnsFalse() {
        val emailAddress = EmailAddress("BadAddress@.com")
        assertFalse(emailAddress.isValid())
    }

    @Test
    fun isValid_WithNoPrefix_ReturnsFalse() {
        val emailAddress = EmailAddress("@gmail.com")
        assertFalse(emailAddress.isValid())
    }

    @Test
    fun isValid_WithValidAddress_ReturnsTrue() {
        val emailAddress = EmailAddress("HelloWorld@gmail.com")
        assertTrue(emailAddress.isValid())
    }
}
