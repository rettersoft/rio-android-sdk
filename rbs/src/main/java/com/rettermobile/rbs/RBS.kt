package com.rettermobile.rbs

import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.auth0.android.jwt.JWT
import com.google.gson.Gson
import com.rettermobile.rbs.service.RBSServiceImp
import com.rettermobile.rbs.service.model.RBSTokenResponse
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.lang.Math.abs


/**
 * Created by semihozkoroglu on 22.11.2020.
 */
class RBS(val applicationContext: Context, val projectId: String, val clientId: String) {

    private val preferences = Preferences(applicationContext)
    private val service = RBSServiceImp()
    private val gson = Gson()

    private var success: ((String?) -> Unit)? = null
    private var error: ((Throwable?) -> Unit)? = null

    private var loginTime: Long = 0L
        get() {
            if (field == 0L) {
                val time = preferences.getLong(Preferences.Keys.LOGIN_TIME, 0L)

                field = time
            }

            return field
        }

    private var tokenInfo: RBSTokenResponse? = null
        get() {
            if (field == null) {
                val infoJson = preferences.getString(Preferences.Keys.TOKEN_INFO)

                if (!TextUtils.isEmpty(infoJson)) {
                    tokenInfo = gson.fromJson(infoJson, RBSTokenResponse::class.java)
                }
            }

            return field
        }
        set(value) {
            field = value

            if (value != null) {
                // Save to device
                preferences.setString(Preferences.Keys.TOKEN_INFO, gson.toJson(value))
                preferences.setLong(Preferences.Keys.LOGIN_TIME, System.currentTimeMillis())
            } else {
                preferences.deleteKey(Preferences.Keys.TOKEN_INFO)
                preferences.deleteKey(Preferences.Keys.LOGIN_TIME)
            }
        }

    fun authenticateWithCustomToken(customToken: String) = runBlocking {
        if (!TextUtils.isEmpty(customToken)) {
            val res = runBlocking { executeRunBlock(customToken = customToken).await() }
        } else {
            error?.invoke(IllegalArgumentException("customToken must not be null or empty"))
        }
    }

    fun sendAction(
        action: String, request: Map<String, Any> = mapOf(),
        success: ((String?) -> Unit)? = null,
        error: ((Throwable?) -> Unit)? = null
    ) {
        this.success = success
        this.error = error

        if (!TextUtils.isEmpty(action)) {
            val res = runBlocking { executeRunBlock(action = action, request = request).await() }

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
                val res = service.getAnonymousToken(projectId, clientId)

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
        val jwt = JWT(tokenInfo!!.accessToken)

        val expiresIn = jwt.getClaim("timestamp").asLong()!!

        return abs(expiresIn - ((System.currentTimeMillis() - loginTime) / 1000)) < 30
    }
}