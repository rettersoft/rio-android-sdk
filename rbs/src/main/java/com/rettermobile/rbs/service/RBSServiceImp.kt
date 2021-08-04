package com.rettermobile.rbs.service

import android.text.TextUtils
import android.util.Log
import com.google.gson.Gson
import com.rettermobile.rbs.service.model.RBSTokenResponse
import com.rettermobile.rbs.util.RBSRegion
import com.rettermobile.rbs.util.getBase64EncodeString
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody

/**
 * Created by semihozkoroglu on 22.11.2020.
 */
class RBSServiceImp constructor(
    val projectId: String,
    val region: RBSRegion,
    sslPinningEnabled: Boolean
) {

    private var networkGet: RBSService = RBSNetwork(sslPinningEnabled).getConnection(region.getUrl)
    private var networkPost: RBSService =
        RBSNetwork(sslPinningEnabled).getConnection(region.postUrl)

    suspend fun executeAction(
        accessToken: String? = null,
        action: String,
        requestJsonString: String,
        headers: Map<String, String>,
        requestType: RequestType
    ): Result<ResponseBody?> {
        Log.e("RBSService", "executeAction $action started")
        return runCatching {
            action.split(".").let {
                if (it.size > 3) {
                    if (TextUtils.equals(it[2], "get")) {
                        val data = requestJsonString.getBase64EncodeString()
                        if (requestType == RequestType.GENERATE_AUTH) {
                            Log.e("RBSService", "generateUrl projectId: $projectId")
                            Log.e("RBSService", "generateUrl action: $action")
                            Log.e("RBSService", "generateUrl accessToken: $accessToken")
                            Log.e("RBSService", "generateUrl headers: ${Gson().toJson(headers)}")
                            Log.e("RBSService", "generateUrl body: $requestJsonString")
                            Log.e("RBSService", "generateUrl bodyEncodeString: $data")

                            ResponseBody.create(
                                MediaType.get("application/json"),
                                region.getUrl + "user/action/$projectId/$action?auth=$accessToken&data=$data"
                            )
                        } else {
                            Log.e("RBSService", "getAction projectId: $projectId")
                            Log.e("RBSService", "getAction action: $action")
                            Log.e("RBSService", "getAction accessToken: $accessToken")
                            Log.e("RBSService", "getAction headers: ${Gson().toJson(headers)}")
                            Log.e("RBSService", "getAction body: $requestJsonString")

                            networkPost.getAction(
                                headers,
                                projectId,
                                action,
                                accessToken!!,
                                data
                            )
                        }
                    } else {
                        val body: RequestBody = RequestBody.create(
                            MediaType.parse("application/json; charset=utf-8"),
                            requestJsonString
                        )

                        Log.e("RBSService", "postAction projectId: $projectId")
                        Log.e("RBSService", "postAction action: $action")
                        Log.e("RBSService", "postAction accessToken: $accessToken")
                        Log.e("RBSService", "postAction headers: ${Gson().toJson(headers)}")
                        Log.e("RBSService", "postAction body: $requestJsonString")

                        networkPost.postAction(headers, projectId, action, accessToken!!, body)
                    }
                } else {
                    throw IllegalStateException("Action not in an acceptable format")
                }
            }
        }
    }

    suspend fun getAnonymousToken(projectId: String): Result<RBSTokenResponse> {
        Log.e("RBSService", "getAnonymous started")
        return kotlin.runCatching { networkGet.anonymousAuth(projectId) }
    }

    suspend fun refreshToken(refreshToken: String): Result<RBSTokenResponse> {
        Log.e("RBSService", "refreshToken started")
        return kotlin.runCatching { networkGet.authRefresh(refreshToken) }
    }

    suspend fun authWithCustomToken(customToken: String): Result<RBSTokenResponse> {
        Log.e("RBSService", "authWithCustomToken started")
        return kotlin.runCatching { networkGet.auth(customToken) }
    }
}