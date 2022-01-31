package com.rettermobile.rio

import android.content.Context
import android.text.TextUtils
import com.rettermobile.rio.service.cloud.RioCloudRequestManager
import com.rettermobile.rio.cloud.RioCloudObject
import com.rettermobile.rio.cloud.RioGetCloudObjectOptions
import com.rettermobile.rio.service.model.exception.CloudNullException
import com.rettermobile.rio.service.model.exception.TokenFailException
import com.rettermobile.rio.model.RioClientAuthStatus
import com.rettermobile.rio.model.RioCulture
import com.rettermobile.rio.model.RioUser
import com.rettermobile.rio.service.RioNetworkConfig
import com.rettermobile.rio.service.auth.RioAuthRequestManager
import com.rettermobile.rio.util.*
import kotlinx.coroutines.*

/**
 * Created by semihozkoroglu on 22.11.2020.
 */
class Rio(
    val applicationContext: Context,
    val projectId: String,
    val culture: RioCulture? = null,
    val config: RioNetworkConfig
) {

    private val job: Job = Job()
    private val scope = CoroutineScope(Dispatchers.Default + job)

    init {
        RioConfig.applicationContext = applicationContext
        RioConfig.projectId = projectId
        RioConfig.culture = culture?.lang
        RioConfig.config = config

        TokenManager.tokenUpdateListener = { sendAuthStatus() }
    }

    private var listener: ((RioClientAuthStatus, RioUser?) -> Unit)? = null

    fun setOnClientAuthStatusChangeListener(l: (RioClientAuthStatus, RioUser?) -> Unit) {
        listener = l

        sendAuthStatus()
    }

    fun authenticateWithCustomToken(customToken: String, error: ((Throwable?) -> Unit)? = null) {
        scope.launch(exceptionHandler) {
            withContext(Dispatchers.Main) {
                listener?.invoke(RioClientAuthStatus.AUTHENTICATING, null)
            }

            if (!TextUtils.isEmpty(customToken)) {
                val res = runCatching { RioAuthRequestManager.authenticate(customToken) }

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

    fun signInAnonymously(onSuccess: (() -> Unit)? = null, onError: ((Throwable?) -> Unit)? = null) {
        scope.launch(exceptionHandler) {
            val res = runCatching { RioAuthRequestManager.signInAnonymously() }

            if (res.isSuccess) {
                withContext(Dispatchers.Main) { onSuccess?.invoke() }
            } else {
                withContext(Dispatchers.Main) { onError?.invoke(res.exceptionOrNull()) }
            }
        }
    }

    fun getCloudObject(
        options: RioGetCloudObjectOptions,
        onSuccess: ((RioCloudObject) -> Unit)? = null,
        onError: ((Throwable?) -> Unit)? = null
    ) {
        scope.launch(exceptionHandler) {
            val res =
                runCatching { RioCloudRequestManager.exec(action = RioActions.INSTANCE, options) }

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
                signOut()
            }
        }
    }

    private fun sendAuthStatus() {
        scope.launch(exceptionHandler) {
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

    fun signOut() {
        scope.launch(signOutExceptionHandler) {
            RioAuthRequestManager.signOut()

            clear()
        }
    }

    private fun clear() {
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

    private val exceptionHandler = CoroutineExceptionHandler { _, e ->
        RioLogger.log("ExceptionHandler: ${e.message} \nStackTrace: ${e.stackTraceToString()}")
    }

    private val signOutExceptionHandler = CoroutineExceptionHandler { _, e ->
        RioLogger.log("SignOutExceptionHandler: ${e.message} \nStackTrace: ${e.stackTraceToString()}")

        clear()
    }
}