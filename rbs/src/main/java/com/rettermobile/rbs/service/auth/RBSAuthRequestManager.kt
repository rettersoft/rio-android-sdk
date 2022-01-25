package com.rettermobile.rbs.service.auth

import com.rettermobile.rbs.RBSLogger
import com.rettermobile.rbs.util.TokenManager

/**
 * Created by semihozkoroglu on 10.12.2021.
 */
object RBSAuthRequestManager {

    // Call with runCatching
    suspend fun authenticate(customToken: String) {
        TokenManager.authenticate(customToken)

        RBSLogger.log("RBSAuthRequestManager.authenticate OK")
    }

    // Call with runCatching
    suspend fun signInAnonymously(): String {
        TokenManager.checkToken()

        RBSLogger.log("RBSAuthRequestManager.signInAnonymously OK")

        return "Success"
    }

    suspend fun signOut(): String {
        val res = RBSAuthServiceImp.signOut()

        RBSLogger.log("RBSAuthRequestManager.signOut OK")

        return if (res.isSuccess) {
            "OK"
        } else {
            "OK"
        }
    }
}