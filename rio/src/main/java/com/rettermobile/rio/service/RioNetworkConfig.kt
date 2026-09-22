package com.rettermobile.rio.service

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.rettermobile.rio.util.RioRegion
import okhttp3.ConnectionSpec
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor

/**
 * Created by semihozkoroglu on 20.01.2022.
 */
class RioNetworkConfig(
    var region: RioRegion? = null,
    var customDomain: String? = null,
    var sslPinningEnabled: Boolean = true,
    var interceptor: Interceptor? = null,
    var networkInterceptor: Interceptor? = null,
    var gson: Gson = GsonBuilder().create(),
    var sslPins: List<Pair<String, String>>? = null,
    var pinnedPemCertificate: String? = null,
    var headerInterceptor: HeaderInterceptor? = null,
    var firebaseEnable: Boolean = true,
    var logLevel: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.BODY,
    var connectionSpec: List<ConnectionSpec>? = null,
    /**
     * Controls what [com.rettermobile.rio.Rio.signOut] reports to its callback.
     *
     * `false` (default) keeps the behaviour every release up to 1.9.1 had: the
     * callback always reports success, whatever the server answered. Existing
     * integrations that navigate on `isSuccess` are unaffected.
     *
     * `true` reports the real outcome - a transport failure, a non-2xx status,
     * or a business error returned inside a 2xx response (for example
     * `code: ACCESS_DENIED` / `jwt expired`) all reach the callback as
     * `isSuccess = false` with a [com.rettermobile.rio.cloud.RioErrorResponse].
     *
     * The local session is cleared either way, so `false` never means the
     * device is still signed in - only that the server may not have revoked
     * the session.
     */
    var strictSignOutResult: Boolean = false
) {
    private fun init(builder: Builder): RioNetworkConfig {
        if (builder.region == null && builder.customDomain == null) {
            throw Exception("Region or customDomain cannot be empty!")
        } else if (builder.customDomain != null && builder.customDomain!!.startsWith("http")) {
            throw Exception("Please enter the custom domain without http or https!")
        }

        return RioNetworkConfig(
            builder.region,
            builder.customDomain,
            builder.sslPinningEnabled,
            builder.interceptor,
            builder.networkInterceptor,
            builder.gson,
            builder.sslPins,
            builder.pinnedPemCertificate,
            builder.headerInterceptor,
            builder.firebaseEnable,
            builder.logLevel,
            builder.connectionSpec,
            builder.strictSignOutResult
        )
    }

    companion object {
        inline fun build(block: Builder.() -> Unit): RioNetworkConfig {
            return Builder().apply(block).build()
        }
    }

    class Builder {
        var region: RioRegion? = null
        var customDomain: String? = null
        var sslPinningEnabled: Boolean = true
        var interceptor: Interceptor? = null
        var networkInterceptor: Interceptor? = null
        var gson: Gson = GsonBuilder().create()
        var sslPins: List<Pair<String, String>>? = null
        var pinnedPemCertificate: String? = null
        var headerInterceptor: HeaderInterceptor? = null
        var firebaseEnable: Boolean = true
        var logLevel: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.BODY
        var connectionSpec: List<ConnectionSpec>? = null
        var strictSignOutResult: Boolean = false

        fun build() = RioNetworkConfig().init(this)
    }

    interface HeaderInterceptor {
        fun headers(): List<Pair<String, String>>
    }
}
