package com.rettermobile.rbs.service

import android.text.TextUtils
import android.util.Log
import com.google.gson.Gson
import com.rettermobile.rbs.model.RBSCulture
import com.rettermobile.rbs.service.model.RBSTokenResponse
import com.rettermobile.rbs.util.Logger
import com.rettermobile.rbs.util.RBSRegion
import com.rettermobile.rbs.util.getBase64EncodeString
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody

/**
 * Created by semihozkoroglu on 22.11.2020.
 */
class RBSServiceImp constructor(
    val projectId: String,
    private val region: RBSRegion,
    sslPinningEnabled: Boolean,
    private val logger: Logger
) {

    private var networkGet: RBSService = RBSNetwork(sslPinningEnabled).getConnection(region.getUrl)
    private var networkPost: RBSService =
        RBSNetwork(sslPinningEnabled).getConnection(region.postUrl)

    suspend fun executeAction(
        accessToken: String? = null,
        action: String,
        requestJsonString: String,
        headers: Map<String, String>,
        culture: RBSCulture? = null,
        requestType: RequestType
    ): Result<ResponseBody?> {
        logger.log("executeAction $action started")
        return runCatching {
            action.split(".").let {
                if (it.size > 3) {
                    if (TextUtils.equals(it[2], "get")) {
                        val data = requestJsonString.getBase64EncodeString()
                        if (requestType == RequestType.GENERATE_AUTH) {
                            logger.log("generateUrl projectId: $projectId")
                            logger.log("generateUrl action: $action")
                            logger.log("generateUrl accessToken: $accessToken")
                            logger.log("generateUrl headers: ${Gson().toJson(headers)}")
                            logger.log("generateUrl body: $requestJsonString")
                            logger.log("generateUrl bodyEncodeString: $data")

                            "${region.getUrl}user/action/$projectId/$action?auth=$accessToken&data=$data".toResponseBody(
                                "application/json".toMediaType()
                            )
                        } else {
                            logger.log("getAction projectId: $projectId")
                            logger.log("getAction action: $action")
                            logger.log("getAction accessToken: $accessToken")
                            logger.log("getAction headers: ${Gson().toJson(headers)}")
                            logger.log("getAction body: $requestJsonString")

                            networkPost.getAction(
                                headers,
                                projectId,
                                action,
                                accessToken!!,
                                data,
                                culture?.culture
                            )
                        }
                    } else {
                        val body: RequestBody =
                            requestJsonString.toRequestBody("application/json; charset=utf-8".toMediaType())

                        logger.log("postAction projectId: $projectId")
                        logger.log("postAction action: $action")
                        logger.log("postAction accessToken: $accessToken")
                        logger.log("postAction headers: ${Gson().toJson(headers)}")
                        logger.log("postAction body: $requestJsonString")

                        networkPost.postAction(headers, projectId, action, accessToken!!, body, culture?.culture)
                    }
                } else {
                    throw IllegalStateException("Action not in an acceptable format")
                }
            }
        }
    }

    suspend fun getAnonymousToken(projectId: String): Result<RBSTokenResponse> {
        logger.log("getAnonymous started")
        return kotlin.runCatching { networkGet.anonymousAuth(projectId) }
    }

    suspend fun refreshToken(refreshToken: String): Result<RBSTokenResponse> {
        logger.log("refreshToken started")
        return kotlin.runCatching { networkGet.authRefresh(refreshToken) }
    }

    suspend fun authWithCustomToken(customToken: String): Result<RBSTokenResponse> {
        logger.log("authWithCustomToken started")
        return kotlin.runCatching { networkGet.auth(customToken) }
    }
}