package com.rettermobile.rbs.service

import com.google.gson.GsonBuilder
import com.rettermobile.rbs.BuildConfig
import com.rettermobile.rbs.RBSConfig
import com.rettermobile.rbs.RBSLogger
import okhttp3.CacheControl
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by semihozkoroglu on 22.11.2020.
 */
class RBSNetwork {

    private var service: RBSService? = null
    private var cloudService: RBSCloudService? = null

    private fun provideCertificate(): CertificatePinner {
        return CertificatePinner.Builder()
            .add("*.rtbs.io", "sha256/++MBgDH5WGvL9Bcn5Be30cRcL0f5O+NyoXuWtQdX1aI=")
            .add("*.rtbs.io", "sha256/f0KW/FtqTjs108NpYj42SrGvOB2PpxIVM8nWxjPqJGE=")
            .add("*.rtbs.io", "sha256/NqvDJlas/GRcYbcWE8S/IceH9cq77kg0jVhZeAPXq8k=")
            .add("*.rtbs.io", "sha256/9+ze1cZgR9KO1kZrVDxA4HQ6voHRCSVNz4RdTCx4U8U=")
            .add("*.rtbs.io", "sha256/KwccWaCgrnaw6tsrrSO61FgLacNgG2MMLq8GE6+oP5I=")
            .add("*.rtbs.io", "sha256/FfFKxFycfaIz00eRZOgTf+Ne4POK6FgYPwhBDqgqxLQ=")
            .add("*.rettermobile.com", "sha256/++MBgDH5WGvL9Bcn5Be30cRcL0f5O+NyoXuWtQdX1aI=")
            .add("*.rettermobile.com", "sha256/f0KW/FtqTjs108NpYj42SrGvOB2PpxIVM8nWxjPqJGE=")
            .add("*.rettermobile.com", "sha256/NqvDJlas/GRcYbcWE8S/IceH9cq77kg0jVhZeAPXq8k=")
            .add("*.rettermobile.com", "sha256/9+ze1cZgR9KO1kZrVDxA4HQ6voHRCSVNz4RdTCx4U8U=")
            .add("*.rettermobile.com", "sha256/KwccWaCgrnaw6tsrrSO61FgLacNgG2MMLq8GE6+oP5I=")
            .add("*.rettermobile.com", "sha256/FfFKxFycfaIz00eRZOgTf+Ne4POK6FgYPwhBDqgqxLQ=")
            .build()
    }

    private fun provideOkHttp(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        val interceptor = HttpLoggingInterceptor() {
            RBSLogger.log(it)
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        if (RBSConfig.sslPinningEnabled) {
            builder.certificatePinner(provideCertificate())
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

        builder.hostnameVerifier { hostname, session -> true }

        return builder.build()
    }

    fun getConnection(serviceUrl: String): RBSService {
        if (service == null) {
            val retrofit = Retrofit.Builder()
                .baseUrl(serviceUrl)
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .client(provideOkHttp())
                .build()

            service = retrofit.create(RBSService::class.java)
        }

        return service!!
    }

    fun getCloudConnection(): RBSCloudService {
        if (cloudService == null) {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://${RBSConfig.projectId}.${RBSConfig.region.cloudApiUrl}")
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .client(provideOkHttp())
                .build()

            cloudService = retrofit.create(RBSCloudService::class.java)
        }

        return cloudService!!
    }
}