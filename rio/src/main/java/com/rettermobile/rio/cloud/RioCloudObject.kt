package com.rettermobile.rio.cloud

import com.rettermobile.rio.RioLogger
import com.rettermobile.rio.service.model.exception.NullBodyException
import com.rettermobile.rio.service.cloud.RioCloudServiceImp
import com.rettermobile.rio.util.RioActions
import com.rettermobile.rio.util.TokenManager
import com.rettermobile.rio.util.parseResponse
import kotlinx.coroutines.*
import okhttp3.ResponseBody
import retrofit2.Response

/**
 * Created by semihozkoroglu on 13.12.2021.
 */
class RioCloudObject constructor(val params: RioCloudObjectParams) {

    var user = RioCloudUserObjectState(params)
    var role = RioCloudRoleObjectState(params)
    var public = RioCloudPublicObjectState(params)

    inline fun <reified T> call(
        options: RioCallMethodOptions,
        noinline onSuccess: ((RioCloudSuccessResponse<T>) -> Unit)? = null,
        noinline onError: ((Throwable?) -> Unit)? = null
    ) {
        GlobalScope.launch {
            async(Dispatchers.IO) {
                val res = runCatching {
                    exec(params, RioActions.CALL, options)
                }

                if (res.isSuccess) {
                    try {
                        val response = res.getOrNull()

                        if (response == null) {
                            withContext(Dispatchers.Main) { onError?.invoke(NullBodyException("null body returned")) }
                        } else {
                            withContext(Dispatchers.Main) {
                                onSuccess?.invoke(
                                    RioCloudSuccessResponse(
                                        response.headers(), response.code(), parseResponse(T::class.java, response.body()?.string())
                                    )
                                )
                            }
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
        objectParams: RioCloudObjectParams,
        action: RioActions,
        options: RioCallMethodOptions
    ): Response<ResponseBody> {
        TokenManager.checkToken()

        val accessToken = TokenManager.accessToken

        val res = RioCloudServiceImp.exec(
            accessToken,
            action,
            RioServiceParam(objectParams, options)
        )

        return if (res.isSuccess) {
            RioLogger.log("RBSCloudManager.exec success")

            res.getOrNull()!!
        } else {
            RioLogger.log(
                "RBSCloudManager.exec fail ${
                    res.exceptionOrNull()?.stackTraceToString()
                }"
            )

            throw res.exceptionOrNull() ?: IllegalAccessError("RBSCloudManager.exec fail")
        }
    }

    fun unsubscribeStates() {
        user.removeListener()
        role.removeListener()
        public.removeListener()
    }
}