package com.rettermobile.rbs.service

import android.util.Log
import com.rettermobile.rbs.service.model.RBSTokenResponse
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject


/**
 * Created by semihozkoroglu on 22.11.2020.
 */
class RBSServiceImp {

    private var network: RBSService = RBSNetwork().getConnection()

    suspend fun executeAction(
        accessToken: String,
        action: String,
        request: Map<String, Any>
    ): Result<ResponseBody?> {
        Log.e("RBSService", "executeAction $action started")
        return kotlin.runCatching {
            val body: RequestBody = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                JSONObject(request).toString()
            )

            network.action(accessToken, action, body)
        }
    }

    suspend fun getAnonymousToken(projectId: String): Result<RBSTokenResponse> {
        Log.e("RBSService", "getAnonymous started")
        return kotlin.runCatching { network.anonymousAuth(projectId) }
    }

    suspend fun refreshToken(refreshToken: String): Result<RBSTokenResponse> {
        Log.e("RBSService", "refreshToken started")
        return kotlin.runCatching { network.authRefresh(refreshToken) }
    }

    suspend fun authWithCustomToken(customToken: String): Result<RBSTokenResponse> {
        Log.e("RBSService", "authWithCustomToken started")
        return kotlin.runCatching { network.auth(customToken) }
    }
}