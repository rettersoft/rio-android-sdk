package com.rettermobile.rio

import android.content.Context
import android.text.TextUtils
import com.rettermobile.rio.cloud.RioCallMethodOptions
import com.rettermobile.rio.cloud.RioCloudObject
import com.rettermobile.rio.cloud.RioCloudObjectOptions
import com.rettermobile.rio.cloud.RioCloudSuccessResponse
import com.rettermobile.rio.model.RioClientAuthStatus
import com.rettermobile.rio.model.RioUser
import com.rettermobile.rio.service.RioNetworkConfig
import com.rettermobile.rio.service.RioRetryConfig
import com.rettermobile.rio.service.auth.RioAuthRequestManager
import com.rettermobile.rio.service.cloud.RioCloudRequestManager
import com.rettermobile.rio.service.model.exception.CloudNullException
import com.rettermobile.rio.util.Logger
import com.rettermobile.rio.util.RioActions
import com.rettermobile.rio.util.TokenData
import com.rettermobile.rio.util.TokenManager
import kotlinx.coroutines.*

private const val USER_SIGN_OUT = "user"
private const val SDK_SIGN_OUT = "sdk"

/**
 * Created by semihozkoroglu on 22.11.2020.
 */
class Rio(
    applicationContext: Context,
    projectId: String,
    culture: String? = null,
    config: RioNetworkConfig,
    retryConfig: RioRetryConfig? = null
) {

    private val job: Job = Job()
    private val scope = CoroutineScope(Dispatchers.Default + job)

    private var authStatus: RioClientAuthStatus? = null

    init {
        RioConfig.applicationContext = applicationContext
        RioConfig.projectId = projectId
        RioConfig.culture = culture ?: "en-us"
        RioConfig.config = config
        RioConfig.retryConfig = retryConfig ?: RioRetryConfig()

        TokenData.tokenUpdateListener = { sendAuthStatus() }
        TokenManager.clearListener = { signOutMethod(type = SDK_SIGN_OUT) }

        TokenData.initialize()
    }

    private var listener: ((RioClientAuthStatus, RioUser?) -> Unit)? = null

    fun setOnClientAuthStatusChangeListener(l: (RioClientAuthStatus, RioUser?) -> Unit) {
        listener = l

        sendAuthStatus()
    }

    fun setOnTokenRefreshListener(l: () -> Unit) {
        TokenManager.tokenRefreshListener = {  }
    }

    fun authenticateWithCustomToken(customToken: String, callback: ((Boolean, Throwable?) -> Unit)? = null) {
        scope.launch(CoroutineExceptionHandler { _, e ->
            RioLogger.log("ExceptionHandler#getCloudObject: ${e.message} \nStackTrace: ${e.stackTraceToString()}")

            callback?.invoke(false, e)
        }) {
            if (authStatus != RioClientAuthStatus.AUTHENTICATING) {
                authStatus = RioClientAuthStatus.AUTHENTICATING
                RioLogger.log("Rio.authenticateWithCustomToken authStatus setted as RioClientAuthStatus.AUTHENTICATING")

                withContext(Dispatchers.Main) {
                    listener?.invoke(RioClientAuthStatus.AUTHENTICATING, null)
                }

                if (!TextUtils.isEmpty(customToken)) {
                    val res = runCatching { RioAuthRequestManager.authenticate(customToken) }

                    if (res.isSuccess) {
                        withContext(Dispatchers.Main) { callback?.invoke(true, null) }
                    } else {
                        withContext(Dispatchers.Main) { callback?.invoke(false, res.exceptionOrNull()) }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        callback?.invoke(
                            false,
                            IllegalArgumentException("customToken must not be null or empty")
                        )
                    }
                }

                delay(3000)
                RioLogger.log("Rio.authenticateWithCustomToken authStatus setted as NULL")
                authStatus = null
            } else {
                RioLogger.log("Rio.authenticateWithCustomToken authStatus is RioClientAuthStatus.AUTHENTICATING")
                delay(3000)
                RioLogger.log("Rio.authenticateWithCustomToken authStatus setted as NULL")
                authStatus = null
            }
        }
    }

    inline fun <reified T> makeStaticCall(
        options: RioCloudObjectOptions,
        noinline onSuccess: ((RioCloudSuccessResponse<T>) -> Unit)? = null,
        noinline onError: ((Throwable?) -> Unit)? = null
    ) {
        if (options.useLocal) {
            RioLogger.log(
                "Rio.makeStaticCall WARNING: useLocal is not honoured on the static call path; " +
                        "the request is always sent remotely. Use getCloudObject to construct a local object."
            )
        }

        RioCloudObject(options)
            .call(
                RioCallMethodOptions(
                    method = options.method,
                    httpMethod = options.httpMethod,
                    body = options.body,
                    headers = options.headers,
                    queries = options.queries,
                    culture = options.culture,
                    path = options.path,
                    type = options.type
                ), onSuccess, onError
            )
    }

    fun getCloudObject(
        options: RioCloudObjectOptions,
        onSuccess: ((RioCloudObject) -> Unit)? = null,
        onError: ((Throwable?) -> Unit)? = null
    ) {
        scope.launch(CoroutineExceptionHandler { _, e ->
            RioLogger.log("ExceptionHandler#getCloudObject: ${e.message} \nStackTrace: ${e.stackTraceToString()}")

            onError?.invoke(e)
        }) {
            val res = runCatching { RioCloudRequestManager.exec(action = RioActions.INSTANCE, options) }

            if (res.isSuccess) {
                if (res.getOrNull() != null) {
                    withContext(Dispatchers.Main) { onSuccess?.invoke(res.getOrNull()!!) }
                } else {
                    withContext(Dispatchers.Main) { onError?.invoke(CloudNullException("Cloud object returned null")) }
                }
            } else {
                withContext(Dispatchers.Main) { onError?.invoke(res.exceptionOrNull()) }
            }
        }
    }

    private fun sendAuthStatus() {
        scope.launch(CoroutineExceptionHandler { _, e ->
            RioLogger.log("ExceptionHandler#sendAuthStatus: ${e.message} \nStackTrace: ${e.stackTraceToString()}")
        }) {
            TokenManager.user()?.let { user ->
                withContext(Dispatchers.Main) {
                    listener?.invoke(
                        RioClientAuthStatus.SIGNED_IN,
                        user
                    )
                }
            } ?: run {
                withContext(Dispatchers.Main) {
                    listener?.invoke(
                        RioClientAuthStatus.SIGNED_OUT,
                        null
                    )
                }
            }
        }
    }

    fun getAuthStatus(): RioClientAuthStatus {
        return TokenManager.user()?.let { user ->
            RioClientAuthStatus.SIGNED_IN
        } ?: run {
            RioClientAuthStatus.SIGNED_OUT
        }
    }

    private fun signOutMethod(type: String, callback: ((Boolean, Throwable?) -> Unit)? = null) {
        RioLogger.log("signOut called $type")

        scope.launch(CoroutineExceptionHandler { _, e ->
            RioLogger.log("ExceptionHandler#signOut: ${e.message} \nStackTrace: ${e.stackTraceToString()}")

            clear()

            callback?.invoke(false, e)
        }) {
            // A user initiated signOut has to reach the server with a valid
            // access token, otherwise the server cannot tell which session to
            // revoke and answers with ACCESS_DENIED / jwt expired. Every cloud
            // call already refreshes here; this path used to skip it.
            //
            // The "sdk" type is triggered from TokenManager.clearListener,
            // which fires *because* the refresh already failed - refreshing
            // again would re-enter that failure and loop - so it is skipped.
            // Skipped when the refresh token is gone or expired too: there is
            // nothing to refresh with, and checkToken would spend four rejected
            // requests finding that out while the user waits on the logout.
            if (type == USER_SIGN_OUT && !TokenData.isRefreshTokenExpired()) {
                runCatching { TokenManager.checkToken() }.onFailure {
                    RioLogger.log("signOut token refresh failed, continuing with sign out: ${it.message}")
                }
            }

            val res = runCatching { RioAuthRequestManager.signOut(type) }

            // The local session is dropped whatever the server answered, so the
            // device never keeps a usable session after signOut.
            clear()

            if (res.isFailure) {
                RioLogger.log("signOut request failed: ${res.exceptionOrNull()?.message}")
            }

            // Reporting the failure is opt-in: integrations written against
            // <= 1.9.1 rely on the callback always reporting success, and
            // gating their navigation on it would strand the user on screen.
            val reportFailure = RioConfig.config.strictSignOutResult && res.isFailure

            withContext(Dispatchers.Main) {
                if (reportFailure) {
                    callback?.invoke(false, res.exceptionOrNull())
                } else {
                    callback?.invoke(true, null)
                }
            }
        }
    }

    fun signOut(callback: ((Boolean, Throwable?) -> Unit)? = null) {
        signOutMethod(type = USER_SIGN_OUT, callback)
    }

    private fun clear() {
        RioLogger.log("clearSession called")

        TokenManager.clear()
        RioFirebaseManager.signOut()
    }

    fun setLoggerListener(listener: Logger) {
        RioLogger.logListener = listener
    }

    fun logEnable(enable: Boolean) {
        RioLogger.logEnable(enable)
    }
}