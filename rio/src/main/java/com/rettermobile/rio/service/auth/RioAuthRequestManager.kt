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

        RioLogger.log("RBSAuthRequestManager.authenticate OK")
    }

    // Call with runCatching
    suspend fun signInAnonymously(): String {
        TokenManager.checkToken()

        RioLogger.log("RBSAuthRequestManager.signInAnonymously OK")

        return "Success"
    }

    suspend fun signOut(): String {
        val res = RioAuthServiceImp.signOut()

        RioLogger.log("RBSAuthRequestManager.signOut OK")

        return if (!res.isFailure) {
            "OK"
        } else {
            "OK"
        }
    }
}