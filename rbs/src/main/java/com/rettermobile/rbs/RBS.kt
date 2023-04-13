package com.rettermobile.rbs

import android.content.Context
import android.text.TextUtils
import com.google.gson.Gson
import com.rettermobile.rbs.cloud.RBSCloudManager
import com.rettermobile.rbs.cloud.RBSCloudObject
import com.rettermobile.rbs.cloud.RBSGetCloudObjectOptions
import com.rettermobile.rbs.exception.CloudNullException
import com.rettermobile.rbs.exception.TokenFailException
import com.rettermobile.rbs.model.RBSClientAuthStatus
import com.rettermobile.rbs.model.RBSCulture
import com.rettermobile.rbs.model.RBSUser
import com.rettermobile.rbs.util.*
import kotlinx.coroutines.*

/**
 * Created by semihozkoroglu on 22.11.2020.
 */
class RBS(
    val applicationContext: Context,
    val projectId: String,
    val config: RBSNetworkConfig
) {

    private val job: Job = Job()
    private val scope = CoroutineScope(Dispatchers.Default + job)

    init {
        RBSConfig.applicationContext = applicationContext
        RBSConfig.projectId = projectId
        RBSConfig.region = config.region
        RBSConfig.sslPinningEnabled = config.sslPinningEnabled
        RBSConfig.interceptor = config.interceptor

        TokenManager.tokenUpdateListener = { sendAuthStatus() }
    }

    private var listener: ((RBSClientAuthStatus, RBSUser?) -> Unit)? = null

    fun setOnClientAuthStatusChangeListener(l: (RBSClientAuthStatus, RBSUser?) -> Unit) {
        listener = l

        sendAuthStatus()
    }

    fun authenticateWithCustomToken(customToken: String, error: ((Throwable?) -> Unit)? = null) {
        scope.launch(exceptionHandler) {
            withContext(Dispatchers.Main) {
                listener?.invoke(RBSClientAuthStatus.AUTHENTICATING, null)
            }

            if (!TextUtils.isEmpty(customToken)) {
                val res = runCatching { RBSRequestManager.authenticate(customToken) }

                if (res.isSuccess) {
                    withContext(Dispatchers.Main) { res }
                } else {
                    withContext(Dispatchers.Main) { error?.invoke(res.exceptionOrNull()) }
                }
            } else {
                withContext(Dispatchers.Main) { error?.invoke(IllegalArgumentException("customToken must not be null or empty")) }
            }
        }
    }

    fun signInAnonymously() {
        RBSLogger.log("signInAnonymously called")
        sendAction(action = RBSActions.SIGN_IN_ANONYMOUS.action)
    }

    fun sendAction(
        action: String,
        data: Map<String, Any> = mapOf(),
        headers: Map<String, String> = mapOf(),
        culture: RBSCulture? = null,
        success: ((String?) -> Unit)? = null,
        error: ((Throwable?) -> Unit)? = null
    ) {
        scope.launch(exceptionHandler) {
            if (!TextUtils.isEmpty(action)) {
                val res = runCatching { RBSRequestManager.exec(action, data, headers, culture) }

                if (res.isSuccess) {
                    withContext(Dispatchers.Main) { success?.invoke(res.getOrNull()) }
                } else {
                    // check if token exception then logout
                    checkTokenException(res.exceptionOrNull())

                    withContext(Dispatchers.Main) { error?.invoke(res.exceptionOrNull()) }
                }
            } else {
                withContext(Dispatchers.Main) { error?.invoke(IllegalArgumentException("action must not be null or empty")) }
            }
        }
    }

    fun getCloudObject(
        options: RBSGetCloudObjectOptions,
        onSuccess: ((RBSCloudObject) -> Unit)? = null,
        onError: ((Throwable?) -> Unit)? = null
    ) {
        scope.launch(exceptionHandler) {
            val res =
                runCatching { RBSCloudManager.exec(action = RBSActions.INSTANCE, options) }

            if (res.isSuccess) {
                if (res.getOrNull() != null) {
                    withContext(Dispatchers.Main) { onSuccess?.invoke(res.getOrNull()!!) }
                } else {
                    withContext(Dispatchers.Main) { onError?.invoke(CloudNullException("Cloud object returned null")) }
                }
            } else {
                // check if token exception then logout
                checkTokenException(res.exceptionOrNull())

                withContext(Dispatchers.Main) { onError?.invoke(res.exceptionOrNull()) }
            }
        }
    }

    private fun checkTokenException(exception: Throwable?) {
        exception?.let {
            if (it is TokenFailException) {
                RBSLogger.log("checkTokenException TokenFailException called")
                signOut()
            }
        }
    }

    fun generateGetActionUrl(
        action: String,
        data: Map<String, Any> = mapOf(),
        success: ((String?) -> Unit)? = null,
        error: ((Throwable?) -> Unit)? = null
    ) {
        scope.launch(exceptionHandler) {
            if (!TextUtils.isEmpty(action)) {
                val res = runCatching { RBSRequestManager.generateUrl(action, data) }

                if (res.isSuccess) {
                    withContext(Dispatchers.Main) { success?.invoke(res.getOrNull()) }
                } else {
                    // check if token exception then logout
                    checkTokenException(res.exceptionOrNull())

                    withContext(Dispatchers.Main) { error?.invoke(res.exceptionOrNull()) }
                }
            } else {
                withContext(Dispatchers.Main) { error?.invoke(IllegalArgumentException("action must not be null or empty")) }
            }
        }
    }

    fun generatePublicGetActionUrl(
        action: String,
        data: Map<String, Any> = mapOf()
    ): String {
        val toJson = Gson().toJson(data)
        val requestJsonEncodedString = toJson.getBase64EncodeString()

        RBSLogger.log("generateUrl public projectId: $projectId")
        RBSLogger.log("generateUrl public action: $action")
        RBSLogger.log("generateUrl public body: $toJson")
        RBSLogger.log("generateUrl public bodyEncodeString: $requestJsonEncodedString")

        return config.region.getUrl + "user/action/$projectId/$action?data=$requestJsonEncodedString"
    }

    private fun sendAuthStatus() {
        scope.launch(exceptionHandler) {
            TokenManager.user()?.let { user ->
                withContext(Dispatchers.Main) {
                    listener?.invoke(
                        user.isAnonymous then RBSClientAuthStatus.SIGNED_IN_ANONYMOUSLY
                            ?: RBSClientAuthStatus.SIGNED_IN,
                        user
                    )
                }
            } ?: run {
                withContext(Dispatchers.Main) {
                    listener?.invoke(
                        RBSClientAuthStatus.SIGNED_OUT,
                        null
                    )
                }
            }
        }
    }

    fun signOut(callback: ((Boolean, Throwable?) -> Unit)? = null) {
        RBSLogger.log("signOut called")
        val request = TokenManager.userId()?.let {
            mapOf(Pair("allTokens", true), Pair("userId", it))
        } ?: kotlin.run { mapOf(Pair("allTokens", true)) }

        sendAction(RBSActions.LOGOUT.action, request, success = {
            RBSLogger.log("signOut RBSActions.LOGOUT OK $it")
            clearSession()
            callback?.invoke(true, null)
        }, error = {
            RBSLogger.log("signOut RBSActions.LOGOUT ERROR ${it?.message ?: it?.stackTraceToString()}")
            clearSession()

            callback?.invoke(false, it)
        })

        clearSession()
    }

    private fun clearSession() {
        RBSLogger.log("clearSession called")
        TokenManager.clear()
        RBSCloudManager.clear()
    }

    fun setLoggerListener(listener: Logger) {
        RBSLogger.logListener = listener
    }

    fun logEnable(enable: Boolean) {
        RBSLogger.logEnable(enable)
    }

    private val exceptionHandler = CoroutineExceptionHandler { _, e ->
        RBSLogger.log("ExceptionHandler: ${e.message} \nStackTrace: ${e.stackTraceToString()}")
    }
}