package com.rettermobile.rio

import android.util.Log
import com.rettermobile.rio.util.Logger

/**
 * Created by semihozkoroglu on 10.12.2021.
 */
object RioLogger {

    var showLog: Boolean = true
    var logListener: Logger? = null

    private val logger = object : Logger {
        override fun log(message: String) {
            if (showLog) {
                Log.v("RBSService", message)
                logListener?.log(message)
            }
        }
    }

    fun log(message: String) {
        logger.log(message = message)
    }

    fun logEnable(enable: Boolean) {
        showLog = enable
    }
}