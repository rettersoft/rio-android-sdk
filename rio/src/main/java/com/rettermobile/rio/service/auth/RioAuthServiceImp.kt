package com.rettermobile.rio.service.auth

import com.rettermobile.rio.RioConfig
import com.rettermobile.rio.RioLogger
import com.rettermobile.rio.service.RioNetwork
import com.rettermobile.rio.service.model.RioTokenModel
import com.rettermobile.rio.service.model.RioTokenResponse
import com.rettermobile.rio.util.TokenManager
import com.rettermobile.rio.util.jwtUserId
import okhttp3.ResponseBody

/**
 * Created by semihozkoroglu on 22.11.2020.
 */
object RioAuthServiceImp {

    private var api: RioAuthService = RioNetwork().getAuthConnection()

    suspend fun getAnonymousToken(): Result<RioTokenResponse> {
        RioLogger.log("getAnonymous started")

        return runCatching { api.anonymous("INSTANCE/ProjectUser", RioConfig.projectId) }
    }

    suspend fun refreshToken(refreshToken: String): Result<RioTokenModel> {
        RioLogger.log("refreshToken started")

        return runCatching { api.refresh("CALL/ProjectUser/refreshToken/${RioConfig.projectId}_${TokenManager.userId}", refreshToken) }
    }

    suspend fun authWithCustomToken(customToken: String): Result<RioTokenModel> {
        RioLogger.log("authWithCustomToken started")

        val userId = customToken.jwtUserId()

        return runCatching { api.auth("CALL/ProjectUser/authWithCustomToken/${RioConfig.projectId}_${userId}", customToken) }
    }

    suspend fun signOut(): Result<ResponseBody> {
        RioLogger.log("authWithCustomToken started")

        return runCatching { api.signOut("CALL/ProjectUser/signOut/${RioConfig.projectId}_${TokenManager.userId}", TokenManager.accessToken) }
    }
}