package com.rettermobile.rbs.cloud

import com.rettermobile.rbs.util.RBSActions
import kotlinx.coroutines.*

/**
 * Created by semihozkoroglu on 13.12.2021.
 */
class RBSCloudObject constructor(val params: RBSCloudObjectParams) {

    var user = RBSCloudUserObjectState(params)
    var role = RBSCloudRoleObjectState(params)
    var public = RBSCloudPublicObjectState(params)

    fun call(
        options: RBSCallMethodOptions,
        onSuccess: ((RBSCloudSuccessResponse?) -> Unit)? = null,
        onError: ((Throwable?) -> Unit)? = null
    ) {
        GlobalScope.launch {
            async(Dispatchers.IO) {
                val res = runCatching {
                    RBSCloudManager.call(
                        params,
                        RBSActions.CALL,
                        options
                    )
                }

                if (res.isSuccess) {
                    withContext(Dispatchers.Main) { onSuccess?.invoke(RBSCloudSuccessResponse(res.getOrNull())) }
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