package com.rettermobile.rbs

import com.google.gson.Gson
import com.rettermobile.rbs.model.RBSCulture
import com.rettermobile.rbs.service.RBSServiceImp
import com.rettermobile.rbs.util.RBSActions
import com.rettermobile.rbs.util.TokenManager
import com.rettermobile.rbs.util.getBase64EncodeString

/**
 * Created by semihozkoroglu on 10.12.2021.
 */
object RBSRequestManager {

    suspend fun authenticate(customToken: String) {
        TokenManager.authenticate(customToken)
    }

    suspend fun generateUrl(
        action: String? = null,
        data: Map<String, Any> = mapOf(),
        headers: Map<String, String>? = null
    ): String {
        TokenManager.checkToken()

        val requestJsonString = Gson().toJson(data)
        val requestEncodedJsonString = requestJsonString.getBase64EncodeString()
        val accessToken = TokenManager.accessToken

        RBSLogger.log("RBSRequestManager.generateUrl projectId: ${RBSConfig.projectId}")
        RBSLogger.log("RBSRequestManager.generateUrl action: $action")
        RBSLogger.log("RBSRequestManager.generateUrl accessToken: $accessToken")
        RBSLogger.log("RBSRequestManager.generateUrl headers: ${Gson().toJson(headers)}")
        RBSLogger.log("RBSRequestManager.generateUrl body: $requestJsonString")
        RBSLogger.log("RBSRequestManager.generateUrl bodyEncodeString: $requestEncodedJsonString")

        return "${RBSConfig.region.getUrl}user/action/${RBSConfig.projectId}/$action?auth=$accessToken&data=$requestEncodedJsonString"
    }

    suspend fun exec(
        action: String? = null,
        data: Map<String, Any> = mapOf(),
        headers: Map<String, String>? = null,
        culture: RBSCulture? = null
    ): String {
        TokenManager.checkToken()

        val requestJsonString = Gson().toJson(data)

        val accessToken = TokenManager.accessToken

        RBSLogger.log("RBSRequestManager.exec projectId: ${RBSConfig.projectId}")
        RBSLogger.log("RBSRequestManager.exec action: $action")
        RBSLogger.log("RBSRequestManager.exec accessToken: $accessToken")
        RBSLogger.log("RBSRequestManager.exec headers: ${Gson().toJson(headers)}")
        RBSLogger.log("RBSRequestManager.exec body: $requestJsonString")

        return if (action == RBSActions.SIGN_IN_ANONYMOUS.action) {
            "Success"
        } else {
            val res = RBSServiceImp.executeAction(
                accessToken,
                action!!,
                requestJsonString,
                headers ?: mapOf(),
                culture
            )

            if (res.isSuccess) {
                RBSLogger.log("RBSRequestManager.exec success")

                val result = res.getOrNull()?.string()

                RBSLogger.log("RBSRequestManager.exec result: $result")

                result ?: ""
            } else {
                RBSLogger.log(
                    "RBSRequestManager.exec fail ${
                        res.exceptionOrNull()?.stackTraceToString()
                    }"
                )

                throw res.exceptionOrNull() ?: IllegalAccessError("RBSRequestManager.exec fail")
            }
        }
    }
}