package com.rettermobile.rbs

import com.rettermobile.rbs.util.RBSRegion
import okhttp3.Interceptor

/**
 * Created by semihozkoroglu on 20.01.2022.
 */
class RBSNetworkConfig(
    var region: RBSRegion = RBSRegion.EU_WEST_1,
    var sslPinningEnabled: Boolean = true,
    var interceptor: Interceptor? = null
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
        var region: RBSRegion = RBSRegion.EU_WEST_1
        var sslPinningEnabled: Boolean = true
        var interceptor: Interceptor? = null

        fun build() = RBSNetworkConfig(this)
    }
}