package com.amin.muteapp.api

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import deakin.gopher.guardian.model.login.SessionManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

object ServiceBuilder {
    var sessionManager: SessionManager? = null
    private var retrofit: Retrofit? = null


    fun buildService(mContext: Context?): Retrofit? {
        sessionManager = SessionManager(mContext!!)

        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)


        val tokenInterceptor = TokenInterceptor()
        val okHttpClient: OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(2, TimeUnit.MINUTES)
            .readTimeout(2, TimeUnit.MINUTES)
            .writeTimeout(2, TimeUnit.MINUTES)
            .addInterceptor(interceptor)
            .addInterceptor(tokenInterceptor)
            .build()
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl("https://guardian-backend.up.railway.app/api/v2/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit
    }


    class TokenInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val newRequest: Request =
                if (!TextUtils.isEmpty(sessionManager?.getToken())) {
                    chain.request().newBuilder()
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/json")
                        .header("Authorization", "Bearer " + sessionManager?.getToken().toString())
                        .build()
                } else {
                    chain.request().newBuilder()
                        .header("Accept", "application/json")
                        .header("Content-Type", "application/json").build()
                }
            return chain.proceed(newRequest)
        }
    }


}