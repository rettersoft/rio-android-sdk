package com.rettermobile.rbs

import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.auth0.android.jwt.JWT
import com.google.gson.Gson
import com.rettermobile.rbs.model.RBSClientAuthStatus
import com.rettermobile.rbs.model.RBSCulture
import com.rettermobile.rbs.model.RBSUser
import com.rettermobile.rbs.service.RBSServiceImp
import com.rettermobile.rbs.service.RequestType
import com.rettermobile.rbs.service.model.RBSTokenResponse
import com.rettermobile.rbs.util.Logger
import com.rettermobile.rbs.util.RBSRegion
import com.rettermobile.rbs.util.getBase64EncodeString
import kotlinx.coroutines.*
import java.util.concurrent.Semaphore


/**
 * Created by semihozkoroglu on 22.11.2020.
 */
class RBS(
    val applicationContext: Context,
    val projectId: String,
    val region: RBSRegion = RBSRegion.EU_WEST_1,
    sslPinningEnabled: Boolean = true
) {

    private var logListener: Logger? = null

    private val availableRest = Semaphore(1, true)

    private val logger = object : Logger {
        override fun log(message: String) {
            Log.e("RBSService", message)
            logListener?.log(message)
        }
    }

    private val preferences = Preferences(applicationContext)
    private val service = RBSServiceImp(projectId, region, sslPinningEnabled, logger)
    private val gson = Gson()

    private var listener: ((RBSClientAuthStatus, RBSUser?) -> Unit)? = null

    private var tokenInfo: RBSTokenResponse? = null
        set(value) {
            field = value

            if (value != null) {
                // Save to device
                preferences.setString(Preferences.Keys.TOKEN_INFO, gson.toJson(value))
            } else {
                // Logout
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

    fun authenticateWithCustomToken(customToken: String, error: ((Throwable?) -> Unit)? = null) {
        GlobalScope.launch {
            async(Dispatchers.IO) {
                withContext(Dispatchers.Main) {
                    listener?.invoke(RBSClientAuthStatus.AUTHENTICATING, null)
                }

                if (!TextUtils.isEmpty(customToken)) {
                    val res =
                        kotlin.runCatching {
                            executeRunBlock(
                                customToken = customToken,
                                requestType = RequestType.REQUEST
                            )
                        }

                    if (res.isSuccess) {
                        withContext(Dispatchers.Main) {
                            res
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            error?.invoke(res.exceptionOrNull())
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        error?.invoke(IllegalArgumentException("customToken must not be null or empty"))
                    }
                }
            }
        }
    }

    fun sendAction(
        action: String,
        data: Map<String, Any> = mapOf(),
        headers: Map<String, String> = mapOf(),
        culture: RBSCulture? = null,
        success: ((String?) -> Unit)? = null,
        error: ((Throwable?) -> Unit)? = null
    ) {
        GlobalScope.launch {
            async(Dispatchers.IO) {
                if (!TextUtils.isEmpty(action)) {
                    val res =
                        kotlin.runCatching {
                            executeRunBlock(
                                action = action,
                                requestJsonString = Gson().toJson(data),
                                headers = headers,
                                culture = culture,
                                requestType = RequestType.REQUEST
                            )
                        }

                    if (res.isSuccess) {
                        withContext(Dispatchers.Main) {
                            success?.invoke(res.getOrNull())
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            error?.invoke(res.exceptionOrNull())
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        error?.invoke(IllegalArgumentException("action must not be null or empty"))
                    }
                }
            }
        }
    }

    fun generateGetActionUrl(
        action: String,
        data: Map<String, Any> = mapOf(),
        success: ((String?) -> Unit)? = null,
        error: ((Throwable?) -> Unit)? = null
    ) {
        GlobalScope.launch {
            async(Dispatchers.IO) {
                if (!TextUtils.isEmpty(action)) {
                    val res =
                        kotlin.runCatching {
                            executeRunBlock(
                                action = action,
                                requestJsonString = Gson().toJson(data),
                                requestType = RequestType.GENERATE_AUTH
                            )
                        }

                    if (res.isSuccess) {
                        withContext(Dispatchers.Main) {
                            success?.invoke(res.getOrNull())
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            error?.invoke(res.exceptionOrNull())
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        error?.invoke(IllegalArgumentException("action must not be null or empty"))
                    }
                }
            }
        }
    }

    fun generatePublicGetActionUrl(
        action: String,
        data: Map<String, Any> = mapOf()
    ): String {
        val toJson = Gson().toJson(data)
        val requestJsonEncodedString = toJson.getBase64EncodeString()

        logger.log("generateUrl public projectId: $projectId")
        logger.log("generateUrl public action: $action")
        logger.log("generateUrl public body: $toJson")
        logger.log("generateUrl public bodyEncodeString: $requestJsonEncodedString")

        return region.getUrl + "user/action/$projectId/$action?data=$requestJsonEncodedString"
    }

    private suspend fun executeRunBlock(
        customToken: String? = null,
        action: String? = null,
        requestJsonString: String? = null,
        headers: Map<String, String>? = null,
        culture: RBSCulture? = null,
        requestType: RequestType
    ): String {
        return exec(customToken, action, requestJsonString, headers, culture, requestType)
    }

    private suspend fun exec(
        customToken: String? = null,
        action: String? = null,
        requestJsonString: String? = null,
        headers: Map<String, String>? = null,
        culture: RBSCulture? = null,
        requestType: RequestType
    ): String {
        // Token info control
        if (!TextUtils.isEmpty(customToken)) {
            val res = service.authWithCustomToken(customToken!!)

            return if (res.isSuccess) {
                logger.log("authWithCustomToken success")

                tokenInfo = res.getOrNull()

                "TOKEN OK"
            } else {
                logger.log("authWithCustomToken fail ${res.exceptionOrNull()?.stackTraceToString()}")

                throw res.exceptionOrNull() ?: IllegalAccessError("AuthWithCustomToken fail")
            }
        } else {
            if (TextUtils.isEmpty(tokenInfo?.accessToken)) {
                val res = service.getAnonymousToken(projectId)

                if (res.isSuccess) {
                    logger.log("getAnonymousToken success")

                    tokenInfo = res.getOrNull()
                } else {
                    logger.log("getAnonymousToken fail")

                    throw res.exceptionOrNull() ?: IllegalAccessError("GetAnonymousToken fail")
                }
            } else {
                availableRest.acquire()

                if (isTokenRefreshRequired()) {
                    val res = service.refreshToken(tokenInfo!!.refreshToken)

                    if (res.isSuccess) {
                        logger.log("refreshToken success")

                        tokenInfo = res.getOrNull()
                    } else {
                        logger.log("refreshToken fail signOut called")
                        signOut()

                        logger.log("refreshToken fail")
                        throw IllegalAccessException("Refresh token expired")
                    }
                }

                availableRest.release()
            }
        }

        val res = service.executeAction(
            tokenInfo!!.accessToken,
            action!!,
            requestJsonString ?: Gson().toJson(null),
            headers ?: mapOf(),
            culture,
            requestType
        )

        return if (res.isSuccess) {
            logger.log("executeAction success")

            res.getOrNull()?.string() ?: ""
        } else {
            logger.log("executeAction fail")
            logger.log("executeAction fail ${res.exceptionOrNull()?.stackTraceToString()}")

            throw res.exceptionOrNull() ?: IllegalAccessError("ExecuteAction fail")
        }
    }

    private fun isTokenRefreshRequired(): Boolean {
        val jwtAccess = JWT(tokenInfo!!.accessToken)
        val accessTokenExpiresAt = jwtAccess.getClaim("exp").asLong()!!

        val jwtRefresh = JWT(tokenInfo!!.refreshToken)
        val refreshTokenExpiresAt = jwtRefresh.getClaim("exp").asLong()!!

        val now = (System.currentTimeMillis() / 1000) + 30

        return now in accessTokenExpiresAt until refreshTokenExpiresAt // now + 280 -> only wait 20 seconds for debugging
    }

    private fun sendAuthStatus() {
        GlobalScope.launch {
            async {
                if (tokenInfo != null) {
                    val jwtAccess = JWT(tokenInfo!!.accessToken)

                    val userId = jwtAccess.getClaim("userId").asString()
                    val anonymous = jwtAccess.getClaim("anonymous").asBoolean()

                    if (anonymous!!) {
                        withContext(Dispatchers.Main) {
                            listener?.invoke(
                                RBSClientAuthStatus.SIGNED_IN_ANONYMOUSLY,
                                RBSUser(userId, anonymous)
                            )
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            listener?.invoke(
                                RBSClientAuthStatus.SIGNED_IN,
                                RBSUser(userId, anonymous)
                            )
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        listener?.invoke(RBSClientAuthStatus.SIGNED_OUT, null)
                    }
                }
            }
        }
    }

    fun signOut() {
        val request = getUserId()?.let {
                mapOf(Pair("allTokens", true), Pair("userId", it))
            } ?: kotlin.run { mapOf(Pair("allTokens", true)) }

        sendAction("rbs.core.request.LOGOUT_USER", request)

        tokenInfo = null
    }

    fun setLoggerListener(listener: Logger) {
        logListener = listener
    }

    private fun getUserId(): String? {
        val jwtAccess = JWT(tokenInfo!!.accessToken)

        return jwtAccess.getClaim("userId").asString()
    }
}