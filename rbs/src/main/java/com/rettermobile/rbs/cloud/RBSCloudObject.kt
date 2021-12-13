package com.rettermobile.rbs.cloud

import com.rettermobile.rbs.util.RBSActions
import kotlinx.coroutines.*

/**
 * Created by semihozkoroglu on 13.12.2021.
 */
class RBSCloudObject constructor(val classId: String, val instanceId: String) {

    var user = RBSCloudUserObjectState(classId, instanceId)
    var role = RBSCloudRoleObjectState(classId, instanceId)
    var public = RBSCloudPublicObjectState(classId, instanceId)

    fun call(
        options: RBSCloudObjectOptions,
        eventFired: ((String?) -> Unit)? = null,
        errorFired: ((Throwable?) -> Unit)? = null
    ) {
        GlobalScope.launch {
            async(Dispatchers.IO) {
                val res = runCatching {
                    RBSCloudManager.call(
                        RBSActions.CALL,
                        options.apply {
                            classId = this@RBSCloudObject.classId
                            instanceId = this@RBSCloudObject.instanceId
                        }
                    )
                }

                if (res.isSuccess) {
                    withContext(Dispatchers.Main) { eventFired?.invoke(res.getOrNull()) }
                } else {
                    withContext(Dispatchers.Main) { errorFired?.invoke(res.exceptionOrNull()) }
                }
            }
        }
    }

    fun getState(
        options: RBSCloudObjectOptions,
        eventFired: ((String?) -> Unit)? = null,
        errorFired: ((Throwable?) -> Unit)? = null
    ) {
        GlobalScope.launch {
            async(Dispatchers.IO) {
                val res = runCatching {
                    RBSCloudManager.call(
                        RBSActions.STATE,
                        options.apply {
                            classId = this@RBSCloudObject.classId
                            instanceId = this@RBSCloudObject.instanceId
                        }
                    )
                }

                if (res.isSuccess) {
                    withContext(Dispatchers.Main) { eventFired?.invoke(res.getOrNull()) }
                } else {
                    withContext(Dispatchers.Main) { errorFired?.invoke(res.exceptionOrNull()) }
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