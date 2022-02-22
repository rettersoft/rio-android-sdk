package com.rettermobile.rio.util

import android.text.TextUtils
import com.auth0.android.jwt.JWT
import com.google.gson.Gson
import com.rettermobile.rio.Preferences
import com.rettermobile.rio.RioFirebaseManager
import com.rettermobile.rio.RioLogger
import com.rettermobile.rio.model.RioUser
import com.rettermobile.rio.service.auth.RioAuthServiceImp
import com.rettermobile.rio.service.cloud.RioCloudRequestManager
import com.rettermobile.rio.service.model.RioTokenModel
import com.rettermobile.rio.service.model.exception.TokenFailException
import retrofit2.HttpException
import java.util.concurrent.Semaphore

/**
 * Created by semihozkoroglu on 10.12.2021.
 */
object TokenManager {
    private val availableRest = Semaphore(1, true)

    var tokenUpdateListener: (() -> Unit)? = null
    var clearListener: (() -> Unit)? = null

    private val gson = Gson()

    val isSignedIn: Boolean
        get() = tokenInfo != null

    val accessToken: String?
        get() {
            return tokenInfo?.accessToken
        }

    private val deltaTime: Long
        get() {
            return Preferences.getLong(Preferences.Keys.TOKEN_INFO_DELTA, 0)
        }

    val userId: String?
        get() = tokenInfo?.accessToken?.jwtUserId()

    val userIdentity: String?
        get() = tokenInfo?.accessToken?.jwtIdentity()

    val user: RioUser?
        get() {
            return tokenInfo?.let {
                val userId = it.accessToken.jwtUserId()
                val anonymous = it.accessToken.jwtAnonymous()

                RioUser(userId, anonymous ?: true)
            } ?: kotlin.run { null }
        }

    private val refreshToken: String?
        get() = tokenInfo?.refreshToken

    private var tokenInfo: RioTokenModel? = null
        set(value) {
            val isStatusChanged = value?.accessToken?.jwtUserId() != userId

            field = value

            if (value != null) {
                // Save to device
                RioLogger.log("TokenManager.setValue save device")
                Preferences.setString(Preferences.Keys.TOKEN_INFO, gson.toJson(value))
            } else {
                // Logout
                RioLogger.log("TokenManager.setValue LOGOUT")
                Preferences.deleteKey(Preferences.Keys.TOKEN_INFO)
                Preferences.deleteKey(Preferences.Keys.TOKEN_INFO_DELTA)
            }

            if (isStatusChanged) {
                RioLogger.log("TokenManager.setValue isStatusChanged: true user:${Gson().toJson(user)}")
                tokenUpdateListener?.invoke()
            } else {
                RioLogger.log("TokenManager.setValue isStatusChanged: false")
            }
        }

    init {
        val infoJson = Preferences.getString(Preferences.Keys.TOKEN_INFO)

        if (!TextUtils.isEmpty(infoJson)) {
            val token = gson.fromJson(infoJson, RioTokenModel::class.java)

            tokenInfo = if (isRefreshTokenExpired(token)) {
                // signOut
                RioLogger.log("TokenManager.init tokenInfo=null")
                null
            } else {
                RioLogger.log("TokenManager.init tokenInfo OK")
                token
            }
        }
    }

    fun calculateDelta() {
        accessToken?.jwtIat()?.let { iat ->
            val diff = (System.currentTimeMillis() / 1000) - iat
            RioLogger.log("TokenManager.tokenInfo set time difference $diff")
            Preferences.setLong(Preferences.Keys.TOKEN_INFO_DELTA, diff)
        }
    }

    private fun isAccessTokenExpired(): Boolean {
        if (isRefreshTokenExpired(tokenInfo!!)) {
            return true
        }

        val jwtAccess = JWT(tokenInfo!!.accessToken)
        val accessTokenExpiresAt = jwtAccess.getClaim("exp").asLong()!!

        val now = (System.currentTimeMillis() / 1000) - deltaTime + 30

        val isExpired =
            now >= accessTokenExpiresAt  // now + 280 -> only wait 20 seconds for debugging

        RioLogger.log("TokenManager.isAccessTokenExpired accessToken: ${tokenInfo!!.accessToken}")
        RioLogger.log("TokenManager.isAccessTokenExpired accessTokenExpiresAt: $accessTokenExpiresAt")
        RioLogger.log("TokenManager.isAccessTokenExpired now: $now")
        RioLogger.log("TokenManager.isAccessTokenExpired isExpired: $isExpired")
        RioLogger.log("TokenManager.isAccessTokenExpired diff: $deltaTime")

        return isExpired
    }

    private fun isRefreshTokenExpired(token: RioTokenModel): Boolean {
        val jwtAccess = JWT(token.refreshToken)
        val refreshTokenExpiresAt = jwtAccess.getClaim("exp").asLong()!!

        val now = (System.currentTimeMillis() / 1000) - deltaTime + 24 * 60 * 60

        val isExpired = now >= refreshTokenExpiresAt  // now + 280 -> only wait 20 seconds for debugging

        RioLogger.log("TokenManager.isRefreshTokenExpired refreshToken: ${token.refreshToken}")
        RioLogger.log("TokenManager.isRefreshTokenExpired refreshTokenExpiresAt: $refreshTokenExpiresAt")
        RioLogger.log("TokenManager.isRefreshTokenExpired now: $now")
        RioLogger.log("TokenManager.isRefreshTokenExpired isExpired: $isExpired")
        RioLogger.log("TokenManager.isRefreshTokenExpired diff: $deltaTime")

        return isExpired
    }

    suspend fun authenticate(customToken: String) {
        val res = RioAuthServiceImp.authWithCustomToken(customToken)

        return if (!res.isFailure) {
            RioLogger.log("authWithCustomToken success")

            tokenInfo = res.getOrNull()
            calculateDelta()

            RioCloudRequestManager.clear()
            RioFirebaseManager.authenticate(tokenInfo?.firebase)
        } else {
            RioLogger.log("authWithCustomToken fail ${res.exceptionOrNull()?.stackTraceToString()}")

            clearListener?.invoke()

            throw res.exceptionOrNull() ?: TokenFailException("AuthWithCustomToken fail")
        }
    }

    suspend fun checkToken() {
        // Token info control
        availableRest.tryAcquire()
        RioLogger.log("TokenManager.checkToken started")

        if (TextUtils.isEmpty(accessToken)) {
            val res = RioAuthServiceImp.getAnonymousToken()

            if (!res.isFailure) {
                RioLogger.log("TokenManager.checkToken getAnonymousToken success")

                tokenInfo = res.getOrNull()?.response
                calculateDelta()

                RioFirebaseManager.authenticate(tokenInfo?.firebase)
            } else {
                RioLogger.log("TokenManager.checkToken getAnonymousToken fail")

                clearListener?.invoke()

                throw res.exceptionOrNull() ?: TokenFailException("AuthWithCustomToken fail")
            }
        } else {
            if (isAccessTokenExpired()) {
                val res = RioAuthServiceImp.refreshToken(refreshToken!!)

                if (!res.isFailure) {
                    RioLogger.log("TokenManager.checkToken refreshToken success")

                    tokenInfo = res.getOrNull()
                    calculateDelta()
                } else {
                    RioLogger.log(" TokenManager.checkToken refreshToken fail signOut called")

                    RioLogger.log("TokenManager.checkToken refreshToken fail")

                    clearListener?.invoke()

                    throw res.exceptionOrNull() ?: TokenFailException("AuthWithCustomToken fail")
                }
            }
        }

        if (RioFirebaseManager.isNotAuthenticated()) {
            RioFirebaseManager.authenticate(tokenInfo?.firebase)
        }

        RioLogger.log("TokenManager.checkToken ended")
        availableRest.release()
    }

    fun clear() {
        RioLogger.log("token cleared")
        tokenInfo = null
    }
}