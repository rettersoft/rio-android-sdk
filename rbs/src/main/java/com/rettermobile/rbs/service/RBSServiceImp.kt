package com.rettermobile.rbs.service

import android.text.TextUtils
import com.google.gson.Gson
import com.rettermobile.rbs.RBSConfig
import com.rettermobile.rbs.RBSLogger
import com.rettermobile.rbs.model.RBSCulture
import com.rettermobile.rbs.service.model.RBSTokenResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody

/**
 * Created by semihozkoroglu on 22.11.2020.
 */
object RBSServiceImp {

    private var networkGet: RBSService = RBSNetwork().getConnection(RBSConfig.region.getUrl)
    private var networkPost: RBSService = RBSNetwork().getConnection(RBSConfig.region.postUrl)

    suspend fun executeAction(
        accessToken: String? = null,
        action: String,
        requestJsonString: String,
        headers: Map<String, String>,
        culture: RBSCulture? = null
    ): Result<ResponseBody?> {
        RBSLogger.log("executeAction $action started")
        return runCatching {
            action.split(".").let {
                if (it.size > 3) {
                    if (TextUtils.equals(it[2], "get")) {
                        RBSLogger.log("getAction projectId: ${RBSConfig.projectId}")
                        RBSLogger.log("getAction action: $action")
                        RBSLogger.log("getAction accessToken: $accessToken")
                        RBSLogger.log("getAction headers: ${Gson().toJson(headers)}")
                        RBSLogger.log("getAction body: $requestJsonString")

                        networkPost.getAction(
                            headers,
                            RBSConfig.projectId,
                            action,
                            accessToken!!,
                            requestJsonString,
                            culture?.culture
                        )
                    } else {
                        val body: RequestBody =
                            requestJsonString.toRequestBody("application/json; charset=utf-8".toMediaType())

                        RBSLogger.log("postAction projectId: ${RBSConfig.projectId}")
                        RBSLogger.log("postAction action: $action")
                        RBSLogger.log("postAction accessToken: $accessToken")
                        RBSLogger.log("postAction headers: ${Gson().toJson(headers)}")
                        RBSLogger.log("postAction body: $requestJsonString")

                        networkPost.postAction(
                            headers,
                            RBSConfig.projectId,
                            action,
                            accessToken!!,
                            body,
                            culture?.culture
                        )
                    }
                } else {
                    throw IllegalStateException("Action not in an acceptable format")
                }
            }
        }
    }

    suspend fun getAnonymousToken(projectId: String): Result<RBSTokenResponse> {
        RBSLogger.log("getAnonymous started")
        return kotlin.runCatching { networkGet.anonymousAuth(projectId) }
    }

    suspend fun refreshToken(refreshToken: String): Result<RBSTokenResponse> {
        RBSLogger.log("refreshToken started")
        return kotlin.runCatching { networkGet.authRefresh(refreshToken) }
    }

    suspend fun authWithCustomToken(customToken: String): Result<RBSTokenResponse> {
        RBSLogger.log("authWithCustomToken started")
        return kotlin.runCatching { networkGet.auth(customToken) }
    }
}