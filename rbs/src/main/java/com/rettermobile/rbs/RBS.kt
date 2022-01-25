package com.rettermobile.rbs

import android.content.Context
import android.text.TextUtils
import com.rettermobile.rbs.service.cloud.RBSCloudRequestManager
import com.rettermobile.rbs.cloud.RBSCloudObject
import com.rettermobile.rbs.cloud.RBSGetCloudObjectOptions
import com.rettermobile.rbs.service.model.exception.CloudNullException
import com.rettermobile.rbs.service.model.exception.TokenFailException
import com.rettermobile.rbs.model.RBSClientAuthStatus
import com.rettermobile.rbs.model.RBSCulture
import com.rettermobile.rbs.model.RBSUser
import com.rettermobile.rbs.service.RBSNetworkConfig
import com.rettermobile.rbs.service.auth.RBSAuthRequestManager
import com.rettermobile.rbs.util.*
import kotlinx.coroutines.*

/**
 * Created by semihozkoroglu on 22.11.2020.
 */
class RBS(
    val applicationContext: Context,
    val projectId: String,
    val culture: RBSCulture? = null,
    val config: RBSNetworkConfig
) {

    private val job: Job = Job()
    private val scope = CoroutineScope(Dispatchers.Default + job)

    init {
        RBSConfig.applicationContext = applicationContext
        RBSConfig.projectId = projectId
        RBSConfig.culture = culture?.lang
        RBSConfig.config = config

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
                val res = runCatching { RBSAuthRequestManager.authenticate(customToken) }

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
            val res = runCatching { RBSAuthRequestManager.signInAnonymously() }

            if (res.isSuccess) {
                withContext(Dispatchers.Main) { onSuccess?.invoke() }
            } else {
                withContext(Dispatchers.Main) { onError?.invoke(res.exceptionOrNull()) }
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
                runCatching { RBSCloudRequestManager.exec(action = RBSActions.INSTANCE, options) }

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

    fun signOut() {
        scope.launch(signOutExceptionHandler) {
            RBSAuthRequestManager.signOut()

            clear()
        }
    }

    private fun clear() {
        TokenManager.clear()
        RBSFirebaseManager.signOut()
        RBSCloudRequestManager.clear()
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

    private val signOutExceptionHandler = CoroutineExceptionHandler { _, e ->
        RBSLogger.log("SignOutExceptionHandler: ${e.message} \nStackTrace: ${e.stackTraceToString()}")

        clear()
    }
}