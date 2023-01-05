package com.rettermobile.rio.util

import android.text.TextUtils
import com.auth0.android.jwt.JWT
import com.google.gson.Gson
import com.rettermobile.rio.Preferences
import com.rettermobile.rio.RioConfig
import com.rettermobile.rio.RioFirebaseManager
import com.rettermobile.rio.RioLogger
import com.rettermobile.rio.model.RioUser
import com.rettermobile.rio.service.auth.RioAuthServiceImp
import com.rettermobile.rio.service.model.RioTokenModel
import com.rettermobile.rio.service.model.exception.TokenFailException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.*

/**
 * Created by semihozkoroglu on 10.12.2021.
 */
object TokenManager {
    private val mutex = Mutex()

    var tokenUpdateListener: (() -> Unit)? = null
    var clearListener: (() -> Unit)? = null

    private val gson = Gson()

    private var tokenInfo: RioTokenModel? = null
        get() {
            val infoJson = Preferences.getString(Preferences.Keys.TOKEN_INFO)

            return gson.fromJson(infoJson, RioTokenModel::class.java)
        }
        set(value) {
            val isStatusChanged = value?.accessToken?.jwtUserId() != userId()

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
                RioLogger.log("TokenManager.setValue isStatusChanged: true user:${Gson().toJson(user())}")
                tokenUpdateListener?.invoke()
            } else {
                RioLogger.log("TokenManager.setValue isStatusChanged: false")
            }
        }

    init {
        val infoJson = Preferences.getString(Preferences.Keys.TOKEN_INFO)

        if (!TextUtils.isEmpty(infoJson)) {
            try {
                val token = gson.fromJson(infoJson, RioTokenModel::class.java)

                if (TextUtils.equals(token.accessToken.projectId(), RioConfig.projectId)) {
                    if (isRefreshTokenExpired(token)) {
                        // signOut
                        tokenInfo = null
                        RioLogger.log("TokenManager.init tokenInfo=null")
                    } else {
                        RioLogger.log("TokenManager.init tokenInfo OK")
                    }
                } else {
                    tokenInfo = null
                    RioLogger.log("TokenManager.init DIFFERENT PROJECT ID!! token setted null!")
                }
            } catch (e: Exception) {
                RioLogger.log("TokenManager.init tokenInfo exception ${e.message}")
            }
        }
    }

    private fun isAccessTokenExpired(): Boolean {
        if (isRefreshTokenExpired(tokenInfo!!)) {
            return true
        }

        val jwtAccess = JWT(tokenInfo!!.accessToken)
        val accessTokenExpiresAt = jwtAccess.getClaim("exp").asLong()!!

        val now = (System.currentTimeMillis() / 1000) - deltaTime() + 30

        val isExpired = now >= accessTokenExpiresAt

        RioLogger.log("TokenManager.isAccessTokenExpired accessToken: ${tokenInfo!!.accessToken}")
        RioLogger.log("TokenManager.isAccessTokenExpired isExpired: $isExpired")

        return isExpired
    }

    private fun isRefreshTokenExpired(token: RioTokenModel): Boolean {
        val jwtAccess = JWT(token.refreshToken)
        val refreshTokenExpiresAt = jwtAccess.getClaim("exp").asLong()!!

        val now = (System.currentTimeMillis() / 1000) - deltaTime() + 24 * 60 * 60

        val isExpired = now >= refreshTokenExpiresAt

        RioLogger.log("TokenManager.isRefreshTokenExpired refreshToken: ${token.refreshToken}")
        RioLogger.log("TokenManager.isRefreshTokenExpired isExpired: $isExpired")

        return isExpired
    }

    suspend fun authenticate(customToken: String) {
        val res = runCatching { RioAuthServiceImp.authWithCustomToken(customToken) }

        return if (res.isSuccess) {
            RioLogger.log("authWithCustomToken success")

            val token = res.getOrNull()

            RioFirebaseManager.authenticate(token?.firebase)

            tokenInfo = token
            calculateDelta()

            RioLogger.log("authWithCustomToken token setted")
        } else {
            RioLogger.log("authWithCustomToken fail ${res.exceptionOrNull()?.stackTraceToString()}")

            clearListener?.invoke()

            throw res.exceptionOrNull() ?: TokenFailException("AuthWithCustomToken fail")
        }
    }

    suspend fun checkToken() {
        // Token info control
        RioLogger.log("TokenManager.checkToken locked")
        mutex.withLock {
            RioLogger.log("TokenManager.checkToken started")

            if (!TextUtils.isEmpty(accessToken())) {
                if (isAccessTokenExpired()) {
                    val refreshToken = tokenInfo?.refreshToken!!

                    // Delete from device
                    RioLogger.log("TokenManager.checkToken delete token info from device")
                    Preferences.deleteKey(Preferences.Keys.TOKEN_INFO)
                    Preferences.deleteKey(Preferences.Keys.TOKEN_INFO_DELTA)

                    refreshWithRetry(refreshToken)
                }
            }

            if (RioFirebaseManager.isNotAuthenticated()) {
                RioFirebaseManager.authenticate(tokenInfo?.firebase)
            }

            RioLogger.log("TokenManager.checkToken ended")
            RioLogger.log("TokenManager.checkToken released")
        }
    }

    private suspend fun refreshWithRetry(refreshToken: String, retryCount: Int = 1) {
        RioLogger.log("TokenManager.retryWithSub retryCount: $retryCount")

        val res = runCatching { RioAuthServiceImp.refreshToken(refreshToken) }

        if (res.isSuccess) {
            RioLogger.log("TokenManager.refreshWithRetry refreshToken success")

            tokenInfo = res.getOrNull()
            calculateDelta()
        } else {
            if (retryCount > 3) {
                RioLogger.log("TokenManager.refreshWithRetry refreshToken fail signOut called")

                RioLogger.log("TokenManager.refreshWithRetry refreshToken fail")

                clearListener?.invoke()

                throw res.exceptionOrNull() ?: TokenFailException("AuthWithCustomToken fail")
            } else {
                Thread.sleep((100 * retryCount).toLong())

                refreshWithRetry(refreshToken, retryCount = retryCount + 1)
            }
        }
    }

    private fun calculateDelta() {
        RioLogger.log("TokenManager.calculateDelta called")

        accessToken()?.jwtIat()?.let { iat ->
            val now = (System.currentTimeMillis() / 1000)

            RioLogger.log("TokenManager.calculateDelta now: $now iat: $iat")

            val diff = now - iat
            RioLogger.log("TokenManager.calculateDelta set time difference $diff")
            Preferences.setLong(Preferences.Keys.TOKEN_INFO_DELTA, diff)
        }
    }

    fun clear() {
        RioLogger.log("token cleared")
        tokenInfo = null
    }

    fun accessToken() = tokenInfo?.accessToken

    private fun deltaTime() = Preferences.getLong(Preferences.Keys.TOKEN_INFO_DELTA, 0)

    fun userId() = tokenInfo?.accessToken?.jwtUserId()

    fun userIdentity() = tokenInfo?.accessToken?.jwtIdentity()

    fun user(): RioUser? {
        return tokenInfo?.let {
            val userId = it.accessToken.jwtUserId()

            RioUser(userId, userId.isNullOrEmpty())
        } ?: kotlin.run { null }
    }

    fun getDeviceId(): String {
        val deviceId = Preferences.getString(Preferences.Keys.DEVICE_ID)

        if (!TextUtils.isEmpty(deviceId)) {
            return deviceId!!
        }

        val uid: String = try {
            UUID.randomUUID().toString()
        } catch (e: Exception) {
            System.currentTimeMillis().toString() + "-" + System.currentTimeMillis().toString()
        }

        Preferences.setString(Preferences.Keys.DEVICE_ID, uid)

        return uid
    }
}