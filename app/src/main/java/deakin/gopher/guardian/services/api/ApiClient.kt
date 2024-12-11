package deakin.gopher.guardian.services.api

import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url
import deakin.gopher.guardian.communication.Message

// Singleton object for Retrofit client
object RetrofitClient {
    // Base URL for API
    private const val BASE_URL = "https://guardian-backend-kz54.onrender.com/api/v1/"

    // Create an interceptor to log network requests/responses for debugging
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // OkHttpClient to handle network calls with the interceptor
    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    // Lazy initialization of Retrofit
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }
}

// Singleton object for making API calls
object ApiClient {
    // Access the API service through Retrofit
    val apiService: ApiService by lazy {
        RetrofitClient.retrofit.create(ApiService::class.java)
    }

    // Get request
    fun get(url: String, onSuccess: (response: ResponseBody) -> Unit, onError: (error: Throwable) -> Unit) {
        val call = apiService.getMessages(url) // <-- Correct method call
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    onSuccess(response.body()!!)
                } else {
                    onError(Throwable("Error: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                onError(t)
            }
        })
    }

    // Post request
    fun post(url: String, message: Message, onSuccess: (response: ResponseBody) -> Unit, onError: (error: Throwable) -> Unit) {
        val call = apiService.sendMessage(url, message) // <-- Correct method call
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    onSuccess(response.body()!!)
                } else {
                    onError(Throwable("Error: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                onError(t)
            }
        })
    }
}

// ApiService interface to define API endpoints
interface ApiService {

    @GET
    fun getMessages(@Url url: String): Call<ResponseBody> // <-- Correct endpoint for GET request

    @POST
    fun sendMessage(@Url url: String, @Body message: Message): Call<ResponseBody> // <-- Correct endpoint for POST request
}
