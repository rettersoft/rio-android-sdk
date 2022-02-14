package com.rettermobile.rio.cloud

import com.rettermobile.rio.service.model.exception.NullBodyException
import com.rettermobile.rio.service.cloud.RioCloudServiceImp
import com.rettermobile.rio.util.RioActions
import com.rettermobile.rio.util.TokenManager
import com.rettermobile.rio.util.parseResponse
import kotlinx.coroutines.*

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
                    TokenManager.checkToken()

                    val accessToken = TokenManager.accessToken

                    RioCloudServiceImp.exec(
                        accessToken,
                        RioActions.CALL,
                        RioServiceParam(params, options)
                    )
                }

                if (res.isSuccess) {
                    try {
                        val response = res.getOrNull()?.getOrNull()

                        if (response == null) {
                            withContext(Dispatchers.Main) { onError?.invoke(NullBodyException("null body returned")) }
                        } else {
                            if (response.isSuccessful) {
                                withContext(Dispatchers.Main) { onSuccess?.invoke(RioCloudSuccessResponse(response.headers(), response.code(), parseResponse(T::class.java, response.body()?.string()))) }
                            } else {
                                withContext(Dispatchers.Main) { onError?.invoke(RioErrorResponse(response.headers(), response.code(), response.errorBody()?.string())) }
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

    fun unsubscribeStates() {
        user.removeListener()
        role.removeListener()
        public.removeListener()
    }
}