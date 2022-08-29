package com.rettermobile.rbs.cloud

import com.rettermobile.rbs.RBSLogger
import com.rettermobile.rbs.exception.NullBodyException
import com.rettermobile.rbs.service.RBSCloudServiceImp
import com.rettermobile.rbs.util.RBSActions
import com.rettermobile.rbs.util.TokenManager
import kotlinx.coroutines.*
import okhttp3.ResponseBody
import retrofit2.Response

/**
 * Created by semihozkoroglu on 13.12.2021.
 */
class RBSCloudObject constructor(val params: RBSCloudObjectParams) {

    var user = RBSCloudUserObjectState(params)
    var role = RBSCloudRoleObjectState(params)
    var public = RBSCloudPublicObjectState(params)

    inline fun <reified T> call(
        options: RBSCallMethodOptions,
        noinline onSuccess: ((RBSCloudSuccessResponse<T>) -> Unit)? = null,
        noinline onError: ((Throwable?) -> Unit)? = null
    ) {
        GlobalScope.launch {
            async(Dispatchers.IO) {
                val res = runCatching {
                    exec(params, RBSActions.CALL, options)
                }

                if (res.isSuccess) {
                    val resObj = RBSCloudSuccessResponse(
                        T::class.java,
                        res.getOrNull()
                    )

                    try {
                        val body = resObj.body()

                        if (body == null) {
                            withContext(Dispatchers.Main) { onError?.invoke(NullBodyException("null body returned")) }
                        } else {
                            withContext(Dispatchers.Main) { onSuccess?.invoke(resObj) }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) { onError?.invoke(e) }
                    }
                } else {
                    withContext(Dispatchers.Main) { onError?.invoke(res.exceptionOrNull()) }
                }
            }
        }
    }

    suspend inline fun exec(
        objectParams: RBSCloudObjectParams,
        action: RBSActions,
        options: RBSCallMethodOptions
    ): Response<ResponseBody> {
        TokenManager.checkToken()

        val accessToken = TokenManager.accessToken()

        val res = RBSCloudServiceImp.exec(
            accessToken,
            action,
            RBSServiceParam(objectParams, options)
        )

        return if (res.isSuccess) {
            RBSLogger.log("RBSCloudManager.exec success")

            res.getOrNull()!!
        } else {
            RBSLogger.log(
                "RBSCloudManager.exec fail ${
                    res.exceptionOrNull()?.stackTraceToString()
                }"
            )

            throw res.exceptionOrNull() ?: IllegalAccessError("RBSCloudManager.exec fail")
        }
    }

    fun unsubscribeStates() {
    }
}