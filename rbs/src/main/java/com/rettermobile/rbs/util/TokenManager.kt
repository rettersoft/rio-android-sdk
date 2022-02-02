package com.rettermobile.rbs.util

import android.text.TextUtils
import com.auth0.android.jwt.JWT
import com.google.gson.Gson
import com.rettermobile.rbs.Preferences
import com.rettermobile.rbs.RBSConfig
import com.rettermobile.rbs.RBSLogger
import com.rettermobile.rbs.cloud.RBSCloudManager
import com.rettermobile.rbs.exception.TokenFailException
import com.rettermobile.rbs.model.RBSUser
import com.rettermobile.rbs.service.RBSServiceImp
import com.rettermobile.rbs.service.model.RBSTokenResponse
import retrofit2.HttpException
import java.util.concurrent.Semaphore

/**
 * Created by semihozkoroglu on 10.12.2021.
 */
object TokenManager {
    private val availableRest = Semaphore(1, true)

    var tokenUpdateListener: (() -> Unit)? = null

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

    val user: RBSUser?
        get() {
            return tokenInfo?.let {
                val userId = it.accessToken.jwtUserId()
                val anonymous = it.accessToken.jwtAnonymous()

                RBSUser(userId, anonymous ?: true)
            } ?: kotlin.run { null }
        }

    private val refreshToken: String?
        get() = tokenInfo?.refreshToken

    private var tokenInfo: RBSTokenResponse? = null
        set(value) {
            val isStatusChanged = value?.accessToken?.jwtUserId() != userId

            field = value

            if (value != null) {
                // Save to device
                RBSLogger.log("TokenManager.setValue save device")

                Preferences.setString(Preferences.Keys.TOKEN_INFO, gson.toJson(value))
            } else {
                // Logout
                RBSLogger.log("TokenManager.setValue LOGOUT")

                Preferences.deleteKey(Preferences.Keys.TOKEN_INFO)
                Preferences.deleteKey(Preferences.Keys.TOKEN_INFO_DELTA)
            }

            if (isStatusChanged) {
                tokenUpdateListener?.invoke()
            }
        }

    init {
        val infoJson = Preferences.getString(Preferences.Keys.TOKEN_INFO)

        if (!TextUtils.isEmpty(infoJson)) {
            val token = gson.fromJson(infoJson, RBSTokenResponse::class.java)

            tokenInfo = if (isRefreshTokenExpired(token)) {
                // signOut
                RBSLogger.log("TokenManager.init tokenInfo=null")
                null
            } else {
                RBSLogger.log("TokenManager.init tokenInfo OK")
                token
            }
        }
    }

    private fun calculateDelta() {
        accessToken?.jwtIat()?.let { iat ->
            val diff = (System.currentTimeMillis() / 1000) - iat
            RBSLogger.log("TokenManager.tokenInfo set time difference $diff")
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

        RBSLogger.log("TokenManager.isAccessTokenExpired accessToken: ${tokenInfo!!.accessToken}")
        RBSLogger.log("TokenManager.isAccessTokenExpired accessTokenExpiresAt: $accessTokenExpiresAt")
        RBSLogger.log("TokenManager.isAccessTokenExpired now: $now")
        RBSLogger.log("TokenManager.isAccessTokenExpired isExpired: $isExpired")
        RBSLogger.log("TokenManager.isAccessTokenExpired diff: $deltaTime")

        return isExpired
    }

    private fun isRefreshTokenExpired(token: RBSTokenResponse): Boolean {
        val jwtAccess = JWT(token.refreshToken)
        val refreshTokenExpiresAt = jwtAccess.getClaim("exp").asLong()!!

        val now = (System.currentTimeMillis() / 1000) - deltaTime + 24 * 60 * 60

        val isExpired = now >= refreshTokenExpiresAt  // now + 280 -> only wait 20 seconds for debugging

        RBSLogger.log("TokenManager.isRefreshTokenExpired refreshToken: ${token.refreshToken}")
        RBSLogger.log("TokenManager.isRefreshTokenExpired refreshTokenExpiresAt: $refreshTokenExpiresAt")
        RBSLogger.log("TokenManager.isRefreshTokenExpired now: $now")
        RBSLogger.log("TokenManager.isRefreshTokenExpired isExpired: $isExpired")
        RBSLogger.log("TokenManager.isRefreshTokenExpired diff: $deltaTime")

        return isExpired
    }

    suspend fun authenticate(customToken: String) {
        val res = RBSServiceImp.authWithCustomToken(customToken)

        return if (res.isSuccess) {
            RBSLogger.log("authWithCustomToken success")

            tokenInfo = res.getOrNull()
            calculateDelta()

            RBSCloudManager.clear()
        } else {
            RBSLogger.log("authWithCustomToken fail ${res.exceptionOrNull()?.stackTraceToString()}")

            res.exceptionOrNull()?.let {
                if (it is HttpException) {
                    if (it.code() >= 500) {
                        throw it
                    } else {
                        throw TokenFailException("AuthWithCustomToken fail")
                    }
                } else {
                    throw TokenFailException("AuthWithCustomToken fail")
                }
            } ?: run { throw TokenFailException("AuthWithCustomToken fail") }
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
                calculateDelta()
            } else {
                RBSLogger.log("TokenManager.checkToken getAnonymousToken fail")

                res.exceptionOrNull()?.let {
                    if (it is HttpException) {
                        if (it.code() >= 500) {
                            throw it
                        } else {
                            throw TokenFailException("AnonymousToken fail")
                        }
                    } else {
                        throw TokenFailException("AnonymousToken fail")
                    }
                } ?: run { throw TokenFailException("AnonymousToken fail") }
            }
        } else {
            if (isAccessTokenExpired()) {
                val res = RBSServiceImp.refreshToken(refreshToken!!)

                if (res.isSuccess) {
                    RBSLogger.log("TokenManager.checkToken refreshToken success")

                    tokenInfo = res.getOrNull()
                    calculateDelta()
                } else {
                    RBSLogger.log(" TokenManager.checkToken refreshToken fail signOut called")

                    RBSLogger.log("TokenManager.checkToken refreshToken fail")

                    res.exceptionOrNull()?.let {
                        if (it is HttpException) {
                            if (it.code() >= 500) {
                                throw it
                            } else {
                                throw TokenFailException("RefreshToken fail")
                            }
                        } else {
                            throw TokenFailException("RefreshToken fail")
                        }
                    } ?: run { throw TokenFailException("RefreshToken fail") }
                }
            }
        }

        RBSLogger.log("TokenManager.checkToken ended")
        availableRest.release()
    }

    fun clear() {
        RBSLogger.log("token cleared")
        tokenInfo = null
    }
}