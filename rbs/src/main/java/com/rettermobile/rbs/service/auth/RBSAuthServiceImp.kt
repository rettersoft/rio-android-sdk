package com.rettermobile.rbs.service.auth

import com.rettermobile.rbs.RBSConfig
import com.rettermobile.rbs.RBSLogger
import com.rettermobile.rbs.service.RBSNetwork
import com.rettermobile.rbs.service.model.RBSTokenModel
import com.rettermobile.rbs.service.model.RBSTokenResponse
import com.rettermobile.rbs.util.TokenManager
import com.rettermobile.rbs.util.jwtUserId
import okhttp3.ResponseBody

/**
 * Created by semihozkoroglu on 22.11.2020.
 */
object RBSAuthServiceImp {

    private var api: RBSAuthService = RBSNetwork().getAuthConnection()

    suspend fun getAnonymousToken(): Result<RBSTokenResponse> {
        RBSLogger.log("getAnonymous started")

        return runCatching { api.anonymous("INSTANCE/ProjectUser", RBSConfig.projectId) }
    }

    suspend fun refreshToken(refreshToken: String): Result<RBSTokenModel> {
        RBSLogger.log("refreshToken started")

        return runCatching { api.refresh("CALL/ProjectUser/refreshToken/${RBSConfig.projectId}_${TokenManager.userId}", refreshToken) }
    }

    suspend fun authWithCustomToken(customToken: String): Result<RBSTokenModel> {
        RBSLogger.log("authWithCustomToken started")

        val userId = customToken.jwtUserId()

        return runCatching { api.auth("CALL/ProjectUser/authWithCustomToken/${RBSConfig.projectId}_${userId}", customToken) }
    }

    suspend fun signOut(): Result<ResponseBody> {
        RBSLogger.log("authWithCustomToken started")

        return runCatching { api.signOut("CALL/ProjectUser/signOut/${RBSConfig.projectId}_${TokenManager.userId}", TokenManager.accessToken) }
    }
}