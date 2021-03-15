package com.rbs.android.example.network

import android.util.Log
import com.google.gson.GsonBuilder
import com.rettermobile.rbs.BuildConfig
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by semihozkoroglu on 15.03.2021.
 */
class TestNetwork {
    private var okHttpClient: OkHttpClient? = null

    private fun provideOkHttp(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        val interceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
            Log.e("OkHttp", it)
        })

        if (BuildConfig.DEBUG) {
            interceptor.level = HttpLoggingInterceptor.Level.BODY
        } else {
            interceptor.level = HttpLoggingInterceptor.Level.NONE
        }

        builder.addInterceptor { chain ->
            val originalRequest = chain.request()

            val newRequestBuilder = originalRequest.newBuilder()

            newRequestBuilder
                .addHeader("Content-Type", "application/json;charset=UTF-8")
                .addHeader("x-rbs-sdk-client", "android")
                .cacheControl(CacheControl.FORCE_NETWORK)

            return@addInterceptor chain.proceed(newRequestBuilder.build())
        }

        val sessionTimeout = 120L

        builder.addInterceptor(interceptor)
        builder.connectTimeout(sessionTimeout, TimeUnit.SECONDS)
        builder.readTimeout(sessionTimeout, TimeUnit.SECONDS)
        builder.writeTimeout(sessionTimeout, TimeUnit.SECONDS)

        builder.hostnameVerifier { _, _ -> true }

        return builder.build()
    }

    fun getConnection(url: String): Service {
        if (okHttpClient == null) {
            okHttpClient = provideOkHttp()
        }

        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .client(okHttpClient!!)
            .build()

        return retrofit.create(Service::class.java)
    }
}