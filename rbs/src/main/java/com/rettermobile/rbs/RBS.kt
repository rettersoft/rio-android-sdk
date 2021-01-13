package com.rettermobile.rbs

import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.auth0.android.jwt.JWT
import com.google.gson.Gson
import com.rettermobile.rbs.model.RBSClientAuthStatus
import com.rettermobile.rbs.model.RBSUser
import com.rettermobile.rbs.service.RBSServiceImp
import com.rettermobile.rbs.service.model.RBSTokenResponse
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking


/**
 * Created by semihozkoroglu on 22.11.2020.
 */
class RBS(
    val applicationContext: Context,
    val projectId: String,
    serviceUrl: String = "https://core.rtbs.io/"
) {

    private val preferences = Preferences(applicationContext)
    private val service = RBSServiceImp(serviceUrl)
    private val gson = Gson()

    private var success: ((String?) -> Unit)? = null
    private var error: ((Throwable?) -> Unit)? = null
    private var listener: ((RBSClientAuthStatus, RBSUser?) -> Unit)? = null

    private var tokenInfo: RBSTokenResponse? = null
        set(value) {
            field = value

            if (value != null) {
                // Save to device
                preferences.setString(Preferences.Keys.TOKEN_INFO, gson.toJson(value))
            } else {
                preferences.deleteKey(Preferences.Keys.TOKEN_INFO)
            }

            sendAuthStatus()
        }

    init {
        val infoJson = preferences.getString(Preferences.Keys.TOKEN_INFO)

        if (!TextUtils.isEmpty(infoJson)) {
            tokenInfo = gson.fromJson(infoJson, RBSTokenResponse::class.java)
        }
    }

    fun setOnClientAuthStatusChangeListener(l: (RBSClientAuthStatus, RBSUser?) -> Unit) {
        listener = l

        sendAuthStatus()
    }

    fun authenticateWithCustomToken(customToken: String) = runBlocking {
        listener?.invoke(RBSClientAuthStatus.AUTHENTICATING, null)

        if (!TextUtils.isEmpty(customToken)) {
            val res = runBlocking { executeRunBlock(customToken = customToken).await() }
        } else {
            error?.invoke(IllegalArgumentException("customToken must not be null or empty"))
        }
    }

    fun sendAction(
        action: String, data: Map<String, Any> = mapOf(),
        success: ((String?) -> Unit)? = null,
        error: ((Throwable?) -> Unit)? = null
    ) {
        this.success = success
        this.error = error

        if (!TextUtils.isEmpty(action)) {
            val res = runBlocking { executeRunBlock(action = action, request = data).await() }

            if (res.isSuccess) {
                success?.invoke(res.getOrNull())
            } else {
                error?.invoke(res.exceptionOrNull())
            }
        } else {
            error?.invoke(IllegalArgumentException("action must not be null or empty"))
        }
    }

    private fun executeRunBlock(
        customToken: String? = null,
        action: String? = null,
        request: Map<String, Any>? = null
    ): Deferred<Result<String>> {
        return GlobalScope.async {
            kotlin.runCatching { exec(customToken, action, request) }
        }
    }

    private suspend fun exec(
        customToken: String? = null,
        action: String? = null,
        request: Map<String, Any>? = null
    ): String {
        if (!TextUtils.isEmpty(customToken)) {
            val res = service.authWithCustomToken(customToken!!)

            return if (res.isSuccess) {
                Log.e("RBSService", "authWithCustomToken success")

                tokenInfo = res.getOrNull()

                "TOKEN OK"
            } else {
                Log.e("RBSService", "authWithCustomToken fail")

                throw res.exceptionOrNull()!!
            }
        } else {
            if (TextUtils.isEmpty(tokenInfo?.accessToken)) {
                val res = service.getAnonymousToken(projectId)

                if (res.isSuccess) {
                    Log.e("RBSService", "getAnonymousToken success")

                    tokenInfo = res.getOrNull()
                } else {
                    Log.e("RBSService", "getAnonymousToken fail")

                    throw res.exceptionOrNull()!!
                }
            } else {
                if (isTokenRefreshRequired()) {
                    val res = service.refreshToken(tokenInfo!!.refreshToken)

                    if (res.isSuccess) {
                        Log.e("RBSService", "refreshToken success")

                        tokenInfo = res.getOrNull()
                    } else {
                        Log.e("RBSService", "refreshToken fail")
                        throw res.exceptionOrNull()!!
                    }
                }
            }

            val res = service.executeAction(tokenInfo!!.accessToken, action!!, request!!)

            return if (res.isSuccess) {
                Log.e("RBSService", "executeAction success")

                val jsonString = res.getOrNull()?.string()

                jsonString ?: ""
            } else {
                Log.e("RBSService", "executeAction fail")

                throw res.exceptionOrNull()!!
            }
        }
    }

    private fun isTokenRefreshRequired(): Boolean {
        val jwtAccess = JWT(tokenInfo!!.accessToken)
        val refreshTokenExpiresAt = jwtAccess.getClaim("exp").asLong()!!

        val jwtRefresh = JWT(tokenInfo!!.refreshToken)
        val accessTokenExpiresAt = jwtRefresh.getClaim("exp").asLong()!!

        val now = System.currentTimeMillis() / 1000

        return !(refreshTokenExpiresAt > now && accessTokenExpiresAt > now)
    }

    private fun sendAuthStatus() {
        if (tokenInfo != null) {
            val jwtAccess = JWT(tokenInfo!!.accessToken)

            val userId = jwtAccess.getClaim("userId").asString()
            val anonymous = jwtAccess.getClaim("anonymous").asBoolean()

            if (anonymous!!) {
                listener?.invoke(
                    RBSClientAuthStatus.SIGNED_IN_ANONYMOUSLY,
                    RBSUser(userId, anonymous)
                )
            } else {
                listener?.invoke(RBSClientAuthStatus.SIGNED_IN, RBSUser(userId, anonymous))
            }
        } else {
            listener?.invoke(RBSClientAuthStatus.SIGNED_OUT, null)
        }
    }

    fun signOut() {
        tokenInfo = null
    }
}