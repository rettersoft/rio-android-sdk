package com.rettermobile.rbs.cloud

import com.rettermobile.rbs.RBSLogger
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

    fun call(
        options: RBSCallMethodOptions,
        onSuccess: ((RBSCloudSuccessResponse) -> Unit)? = null,
        onError: ((Throwable?) -> Unit)? = null
    ) {
        GlobalScope.launch {
            async(Dispatchers.IO) {
                val res = runCatching {
                    exec(params, RBSActions.CALL, options)
                }

                if (res.isSuccess) {
                    withContext(Dispatchers.Main) { onSuccess?.invoke(RBSCloudSuccessResponse(res.getOrNull())) }
                } else {
                    withContext(Dispatchers.Main) { onError?.invoke(res.exceptionOrNull()) }
                }
            }
        }
    }

    private suspend fun exec(
        objectParams: RBSCloudObjectParams,
        action: RBSActions,
        options: RBSCallMethodOptions
    ): Response<ResponseBody> {
        TokenManager.checkToken()

        val accessToken = TokenManager.accessToken

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
        user.removeListener()
        role.removeListener()
        public.removeListener()
    }
}