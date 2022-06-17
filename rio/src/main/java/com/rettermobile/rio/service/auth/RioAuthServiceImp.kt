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

    suspend fun getAnonymousToken(): RioTokenResponse {
        RioLogger.log("getAnonymous started")

        return api.anonymous("root/INSTANCE/ProjectUser", RioConfig.projectId, RioConfig.culture)
    }

    suspend fun refreshToken(refreshToken: String): RioTokenModel {
        RioLogger.log("refreshToken started")

        return api.refresh("root/CALL/ProjectUser/refreshToken/${RioConfig.projectId}_${TokenManager.userId}", refreshToken, RioConfig.culture)
    }

    suspend fun authWithCustomToken(customToken: String): RioTokenModel {
        RioLogger.log("authWithCustomToken started")

        val userId = customToken.jwtUserId()

        return api.auth(
            "root/CALL/ProjectUser/authWithCustomToken/${RioConfig.projectId}${
                if (userId.isNullOrEmpty()) {
                    ""
                } else {
                    "_$userId"
                }
            }", customToken, RioConfig.culture
        )
    }

    suspend fun signOut(): ResponseBody {
        RioLogger.log("authWithCustomToken started")

        return api.signOut("root/CALL/ProjectUser/signOut/${RioConfig.projectId}_${TokenManager.userId}", TokenManager.accessToken, RioConfig.culture)
    }
}