package com.rettermobile.rbs.service

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
 * Created by semihozkoroglu on 22.11.2020.
 */
class RBSNetwork constructor(private val serviceUrl: String) {

    private var service: RBSService? = null
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

    fun getConnection(): RBSService {
        if (service == null) {
            if (okHttpClient == null) {
                okHttpClient = provideOkHttp()
            }

            val retrofit = Retrofit.Builder()
                .baseUrl(serviceUrl)
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .client(okHttpClient!!)
                .build()

            service = retrofit.create(RBSService::class.java)
        }

        return service!!
    }

}