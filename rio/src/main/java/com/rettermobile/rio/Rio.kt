package com.rettermobile.rio

import android.content.Context
import android.text.TextUtils
import com.rettermobile.rio.service.cloud.RioCloudRequestManager
import com.rettermobile.rio.cloud.RioCloudObject
import com.rettermobile.rio.cloud.RioCloudObjectOptions
import com.rettermobile.rio.service.model.exception.CloudNullException
import com.rettermobile.rio.model.RioClientAuthStatus
import com.rettermobile.rio.model.RioUser
import com.rettermobile.rio.service.RioNetworkConfig
import com.rettermobile.rio.service.auth.RioAuthRequestManager
import com.rettermobile.rio.util.*
import kotlinx.coroutines.*

/**
 * Created by semihozkoroglu on 22.11.2020.
 */
class Rio(applicationContext: Context, projectId: String, culture: String? = null, config: RioNetworkConfig) {

    private val job: Job = Job()
    private val scope = CoroutineScope(Dispatchers.Default + job)

    init {
        RioConfig.applicationContext = applicationContext
        RioConfig.projectId = projectId
        RioConfig.culture = culture ?: "en-us"
        RioConfig.config = config

        TokenManager.tokenUpdateListener = { sendAuthStatus() }
        TokenManager.clearListener = { signOut() }
    }

    private var listener: ((RioClientAuthStatus, RioUser?) -> Unit)? = null

    fun setOnClientAuthStatusChangeListener(l: (RioClientAuthStatus, RioUser?) -> Unit) {
        listener = l

        sendAuthStatus()
    }

    fun authenticateWithCustomToken(customToken: String, callback: ((Boolean, Throwable?) -> Unit)? = null) {
        scope.launch(CoroutineExceptionHandler { _, e ->
            RioLogger.log("ExceptionHandler#getCloudObject: ${e.message} \nStackTrace: ${e.stackTraceToString()}")

            callback?.invoke(false, e)
        }) {
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
                withContext(Dispatchers.Main) { callback?.invoke(false, IllegalArgumentException("customToken must not be null or empty")) }
            }
        }
    }

    fun signInAnonymously(callback: ((Boolean, Throwable?) -> Unit)? = null) {
        scope.launch(CoroutineExceptionHandler { _, e ->
            RioLogger.log("ExceptionHandler#signInAnonymously: ${e.message} \nStackTrace: ${e.stackTraceToString()}")

            callback?.invoke(false, e)
        }) {
            val res = runCatching { RioAuthRequestManager.signInAnonymously() }

            if (res.isSuccess) {
                withContext(Dispatchers.Main) { callback?.invoke(true, null) }
            } else {
                withContext(Dispatchers.Main) { callback?.invoke(false, res.exceptionOrNull()) }
            }
        }
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
            TokenManager.user?.let { user ->
                withContext(Dispatchers.Main) {
                    listener?.invoke(
                        user.isAnonymous then RioClientAuthStatus.SIGNED_IN_ANONYMOUSLY
                            ?: RioClientAuthStatus.SIGNED_IN,
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

    fun signOut(callback: ((Boolean, Throwable?) -> Unit)? = null) {
        RioLogger.log("signOut called")

        scope.launch(CoroutineExceptionHandler { _, e ->
            RioLogger.log("ExceptionHandler#signOut: ${e.message} \nStackTrace: ${e.stackTraceToString()}")

            clear()

            callback?.invoke(false, e)
        }) {
            val res = runCatching { RioAuthRequestManager.signOut() }

            clear()

            if (res.isSuccess) {
                withContext(Dispatchers.Main) { callback?.invoke(true, null) }
            } else {
                withContext(Dispatchers.Main) { callback?.invoke(false, res.exceptionOrNull()) }
            }
        }
    }

    private fun clear() {
        RioLogger.log("clearSession called")

        TokenManager.clear()
        RioFirebaseManager.signOut()
        RioCloudRequestManager.clear()
    }

    fun setLoggerListener(listener: Logger) {
        RioLogger.logListener = listener
    }

    fun logEnable(enable: Boolean) {
        RioLogger.logEnable(enable)
    }
}