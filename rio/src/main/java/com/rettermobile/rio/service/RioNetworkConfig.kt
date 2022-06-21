package com.rettermobile.rio.service

import com.rettermobile.rio.util.RioRegion
import okhttp3.Interceptor

/**
 * Created by semihozkoroglu on 20.01.2022.
 */
class RioNetworkConfig(
    var region: RioRegion? = null,
    var customDomain: String? = null,
    var sslPinningEnabled: Boolean = true,
    var interceptor: Interceptor? = null,
    var firebaseEnable: Boolean = true,
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
            builder.interceptor
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

        fun build() = RioNetworkConfig().init(this)
    }
}