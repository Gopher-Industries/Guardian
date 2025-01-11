package deakin.gopher.guardian.services

import android.content.Context
import android.content.Intent
import deakin.gopher.guardian.model.login.EmailAddress
import deakin.gopher.guardian.model.login.Password
import deakin.gopher.guardian.model.login.SessionManager
import deakin.gopher.guardian.services.api.ApiClient
import deakin.gopher.guardian.services.api.ApiService

import deakin.gopher.guardian.view.general.LoginActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class EmailPasswordAuthService(
    private val emailAddress: EmailAddress,
    private val password: Password,
) {
    private val apiService: ApiService =ApiClient.apiService

    suspend fun signIn(): Response<Any>? {
        return try {
            withContext(Dispatchers.IO) {
                apiService.signIn(emailAddress.emailAddress, password.password)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun createAccount(): Response<Any>? {
        return try {
            withContext(Dispatchers.IO) {
                apiService.createAccount(emailAddress.emailAddress, password.password)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    companion object {
        suspend fun resetPassword(emailAddress: EmailAddress): Response<Any>? {
            return try {
                withContext(Dispatchers.IO) {
                    ApiClient.apiService
                        .resetPassword(emailAddress.emailAddress)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        fun signOut(context: Context) {
            try {
                SessionManager.logoutUser()
                // Perform any backend logout logic if needed
                context.startActivity(Intent(context, LoginActivity::class.java))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
