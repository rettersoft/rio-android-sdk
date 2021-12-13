package com.rettermobile.rbs.util

import android.text.TextUtils
import com.auth0.android.jwt.JWT
import com.google.gson.Gson
import com.rettermobile.rbs.Preferences
import com.rettermobile.rbs.RBSConfig
import com.rettermobile.rbs.RBSFirebaseManager
import com.rettermobile.rbs.RBSLogger
import com.rettermobile.rbs.exception.TokenFailException
import com.rettermobile.rbs.model.RBSUser
import com.rettermobile.rbs.service.RBSServiceImp
import com.rettermobile.rbs.service.model.RBSTokenResponse
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.Semaphore

/**
 * Created by semihozkoroglu on 10.12.2021.
 */
object TokenManager {
    private val availableRest = Semaphore(1, true)

    var tokenUpdateListener: (() -> Unit)? = null

    private val gson = Gson()

    val accessToken: String?
        get() {
            return tokenInfo?.accessToken
        }

    val userId: String?
        get() {
            return accessToken?.let {
                val jwtAccess = JWT(it)

                jwtAccess.getClaim("userId").asString()
            } ?: run { null }
        }

    val userIdentity: String?
        get() {
            return accessToken?.let {
                val jwtAccess = JWT(it)

                jwtAccess.getClaim("identity").asString()
            } ?: run { null }
        }

    val user: RBSUser?
        get() {
            return tokenInfo?.let {
                val jwtAccess = JWT(accessToken!!)

                val userId = jwtAccess.getClaim("userId").asString()
                val anonymous = jwtAccess.getClaim("anonymous").asBoolean()

                RBSUser(userId, anonymous ?: true)
            } ?: kotlin.run { null }
        }

    val refreshToken: String?
        get() {
            return tokenInfo?.refreshToken
        }

    private var tokenInfo: RBSTokenResponse? = null
        set(value) {
            field = value

            if (value != null) {
                // Save to device
                Preferences.setString(Preferences.Keys.TOKEN_INFO, gson.toJson(value))
            } else {
                // Logout
                Preferences.deleteKey(Preferences.Keys.TOKEN_INFO)
            }

            tokenUpdateListener?.invoke()
        }

    init {
        val infoJson = Preferences.getString(Preferences.Keys.TOKEN_INFO)

        if (!TextUtils.isEmpty(infoJson)) {
            tokenInfo = gson.fromJson(infoJson, RBSTokenResponse::class.java)
        }
    }

    private fun isTokenRefreshRequired(): Boolean {
        val jwtAccess = JWT(accessToken!!)
        val accessTokenExpiresAt = jwtAccess.getClaim("exp").asLong()!!

        val jwtRefresh = JWT(refreshToken!!)
        val refreshTokenExpiresAt = jwtRefresh.getClaim("exp").asLong()!!

        val now = (System.currentTimeMillis() / 1000) + 30

        return now in accessTokenExpiresAt until refreshTokenExpiresAt // now + 280 -> only wait 20 seconds for debugging
    }

    suspend fun authenticate(customToken: String) {
        val res = RBSServiceImp.authWithCustomToken(customToken)

        return if (res.isSuccess) {
            RBSLogger.log("authWithCustomToken success")

            tokenInfo = res.getOrNull()

            RBSFirebaseManager.authenticate(tokenInfo?.firebase)
        } else {
            RBSLogger.log("authWithCustomToken fail ${res.exceptionOrNull()?.stackTraceToString()}")

            throw TokenFailException("AuthWithCustomToken fail")
        }
    }

    suspend fun checkToken() {
        // Token info control
        availableRest.tryAcquire()
        RBSLogger.log("TokenManager.checkToken started")

        if (TextUtils.isEmpty(accessToken)) {
            val res = RBSServiceImp.getAnonymousToken(RBSConfig.projectId)

            if (res.isSuccess) {
                RBSLogger.log("TokenManager.checkToken getAnonymousToken success")

                tokenInfo = res.getOrNull()

                RBSFirebaseManager.authenticate(tokenInfo?.firebase)
            } else {
                RBSLogger.log("TokenManager.checkToken getAnonymousToken fail")

                throw TokenFailException("AnonymousToken fail")
            }
        } else {
            if (isTokenRefreshRequired()) {
                val res = RBSServiceImp.refreshToken(refreshToken!!)

                if (res.isSuccess) {
                    RBSLogger.log("TokenManager.checkToken refreshToken success")

                    tokenInfo = res.getOrNull()
                } else {
                    RBSLogger.log(" TokenManager.checkToken refreshToken fail signOut called")

                    RBSLogger.log("TokenManager.checkToken refreshToken fail")
                    throw TokenFailException("RefreshToken fail")
                }
            }
        }

        if (RBSFirebaseManager.isNotAuthenticated()) {
            RBSFirebaseManager.authenticate(tokenInfo?.firebase)
        }

        RBSLogger.log("TokenManager.checkToken ended")
        availableRest.release()
    }

    fun clear() {
        RBSLogger.log("token cleared")
        tokenInfo = null

        RBSFirebaseManager.signOut()
    }
}