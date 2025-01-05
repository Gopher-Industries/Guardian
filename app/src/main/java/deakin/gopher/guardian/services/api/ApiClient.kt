package deakin.gopher.guardian.services.api

import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import deakin.gopher.guardian.communication.Message

// Singleton object for Retrofit client
object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:3000/api/v1/"

    private val client = OkHttpClient()
    private val interceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    private val clientBuilder = client.newBuilder().addInterceptor(interceptor)

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }
}

object ApiClient {
    @JvmStatic
    val apiService: ApiService by lazy {
        RetrofitClient.retrofit.create(ApiService::class.java)
    }

    // Retrofit GET request
    fun get(url: String, onSuccess: (response: ResponseBody) -> Unit, onError: (error: Throwable) -> Unit) {
        val call = apiService.getMessages(url)
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

    // Retrofit POST request with Message object
    fun post(url: String, message: Message, onSuccess: (response: ResponseBody) -> Unit, onError: (error: Throwable) -> Unit) {
        val call = apiService.sendMessage(url, message)
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

    // New method for deleting a patient
    fun deletePatient(patientId: String, onSuccess: (response: ResponseBody) -> Unit, onError: (error: Throwable) -> Unit) {
        val call = apiService.deletePatient(patientId)
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

    // Optional: Method to get the Retrofit instance if needed
    fun getClient(): Retrofit {
        return RetrofitClient.retrofit
    }
}
