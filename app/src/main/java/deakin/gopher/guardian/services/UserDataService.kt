package deakin.gopher.guardian.services

import android.util.Log
import deakin.gopher.guardian.model.AuthResponse
import deakin.gopher.guardian.model.RegisterRequest

import deakin.gopher.guardian.services.api.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserDataService {
    private val apiService = ApiClient.apiService

    fun create(userId: String, email: String, name: String, password: String, role: String) {
        // Create a RegisterRequest object without including userId
        val request = RegisterRequest(
            email = email,
            name = name,
            password = password,
            role = role,
        )

        apiService.register(request).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful) {
                    // Log the userId for debugging, even though it's not part of the request
                    Log.v("UserDataService", "Created User $userId successfully: ${response.body()}")
                } else {
                    Log.e("UserDataService", "Failed to create user $userId: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                Log.e("UserDataService", "API call failed for userId $userId: $t")
            }
        })
    }
}