package deakin.gopher.guardian.services.api

import com.google.gson.GsonBuilder
import deakin.gopher.guardian.model.register.User
import deakin.gopher.guardian.model.register.UserDeserializer
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
//    private const val BASE_URL = "http://10.0.2.2:3000/api/v1/"

    private const val BASE_URL = "https://the-real-guardian-backend.vercel.app/api/v1/"

//  private const val BASE_URL = "https://guardian-backend-git-fix-cors-patelrudra2306-5873s-projects.vercel.app/api/v1/"

    private val gson =
        GsonBuilder()
            .registerTypeAdapter(User::class.java, UserDeserializer())
            .setLenient()
            .create()

    private val client = OkHttpClient()
    private val interceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    private val clientBuilder = client.newBuilder().addInterceptor(interceptor)

    val retrofit: Retrofit by lazy {
        Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create(gson))
            .client(clientBuilder.build()).build()
    }
}

object ApiClient {
    val apiService: ApiService by lazy {
        RetrofitClient.retrofit.create(ApiService::class.java)
    }
}
