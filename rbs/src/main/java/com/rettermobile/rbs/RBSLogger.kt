package com.rettermobile.rbs

import android.util.Log
import com.rettermobile.rbs.util.Logger

/**
 * Created by semihozkoroglu on 10.12.2021.
 */
object RBSLogger {

    var showLog: Boolean = true
    var logListener: Logger? = null

    private val logger = object : Logger {
        override fun log(message: String) {
            if (showLog) {
                Log.e("RBSService", message)
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