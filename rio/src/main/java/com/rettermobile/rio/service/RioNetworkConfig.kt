package com.rettermobile.rio.service

import com.rettermobile.rio.util.RioRegion
import okhttp3.Interceptor

/**
 * Created by semihozkoroglu on 20.01.2022.
 */
class RioNetworkConfig(
    var region: RioRegion = RioRegion.EU_WEST_1,
    var sslPinningEnabled: Boolean = true,
    var interceptor: Interceptor? = null,
    var firebaseEnable: Boolean = true,
) {
    private constructor(builder: Builder) : this(
        builder.region,
        builder.sslPinningEnabled,
        builder.interceptor
    )

    companion object {
        inline fun build(block: Builder.() -> Unit) = Builder().apply(block).build()
    }

    class Builder {
        var region: RioRegion = RioRegion.EU_WEST_1
        var sslPinningEnabled: Boolean = true
        var interceptor: Interceptor? = null

        fun build() = RioNetworkConfig(this)
    }
}