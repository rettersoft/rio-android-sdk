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
class RBSServiceImp constructor(val projectId: String, val region: RBSRegion) {

    private var networkGet: RBSService = RBSNetwork().getConnection(region.getUrl)
    private var networkPost: RBSService = RBSNetwork().getConnection(region.postUrl)

    suspend fun executeAction(
        accessToken: String,
        action: String,
        request: Map<String, Any>,
        headers: Map<String, String>,
        isGenerate: Boolean = false
    ): Result<ResponseBody?> {
        Log.e("RBSService", "executeAction $action started")
        return runCatching {
            action.split(".").let {
                if (it.size > 3) {
                    if (TextUtils.equals(it[2], "get")) {
                        val data = Gson().toJson(request).getBase64EncodeString()
                        if (isGenerate) {
                            ResponseBody.create(
                                MediaType.get("application/json"),
                                region.getUrl + "user/action/$projectId/$action?auth=$accessToken&data=$data"
                            )
                        } else {
                            networkGet.getAction(
                                headers,
                                projectId,
                                action,
                                accessToken,
                                data
                            )
                        }
                    } else {
                        val body: RequestBody = RequestBody.create(
                            MediaType.parse("application/json; charset=utf-8"),
                            Gson().toJson(request)
                        )

                        networkPost.postAction(headers, projectId, action, accessToken, body)
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