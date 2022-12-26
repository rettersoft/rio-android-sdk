package com.rettermobile.rio.service.auth

import com.rettermobile.rio.RioLogger
import com.rettermobile.rio.util.TokenManager

/**
 * Created by semihozkoroglu on 10.12.2021.
 */
object RioAuthRequestManager {

    // Call with runCatching
    suspend fun authenticate(customToken: String) {
        TokenManager.authenticate(customToken)

        RioLogger.log("RIOAuthRequestManager.authenticate OK")
    }

    suspend fun signOut(type: String): String {
        val res = runCatching { RioAuthServiceImp.signOut(type) }

        RioLogger.log("RIOAuthRequestManager.signOut success: ${res.isSuccess}")

        return "OK"
    }
}