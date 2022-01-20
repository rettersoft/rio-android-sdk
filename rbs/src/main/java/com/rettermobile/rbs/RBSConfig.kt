package com.rettermobile.rbs

import android.content.Context
import com.rettermobile.rbs.util.RBSRegion
import okhttp3.Interceptor

/**
 * Created by semihozkoroglu on 10.12.2021.
 */
object RBSConfig {
    lateinit var applicationContext: Context
    lateinit var projectId: String
    var region: RBSRegion = RBSRegion.EU_WEST_1
    var sslPinningEnabled: Boolean = true
    var interceptor: Interceptor? = null
}