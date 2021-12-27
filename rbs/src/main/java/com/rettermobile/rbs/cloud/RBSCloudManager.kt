package com.rettermobile.rbs.cloud

import com.google.gson.Gson
import com.rettermobile.rbs.RBSLogger
import com.rettermobile.rbs.service.RBSCloudServiceImp
import com.rettermobile.rbs.service.model.RBSInstanceResponse
import com.rettermobile.rbs.util.RBSActions
import com.rettermobile.rbs.util.TokenManager

/**
 * Created by semihozkoroglu on 10.12.2021.
 */
object RBSCloudManager {

    private val cloudObjects = arrayListOf<RBSCloudObject>()

    suspend fun exec(
        action: RBSActions,
        options: RBSGetCloudObjectOptions
    ): RBSCloudObject {
        TokenManager.checkToken()

        val foundedObj = cloudObjects.find { it.params.instanceId == options.instanceId }
        return foundedObj?.let {
            RBSLogger.log("RBSCloudManager.exec cloudObjects returned from list")
            it
        } ?: kotlin.run {
            if (options.useLocal && !options.instanceId.isNullOrEmpty()) {
                RBSLogger.log("RBSCloudManager.exec create cloud object in-memory")
                RBSCloudObject(
                    RBSCloudObjectParams(
                        options.classId!!,
                        options.instanceId!!,
                        useLocal = true
                    )
                )
            } else {
                val accessToken = TokenManager.accessToken

                RBSLogger.log("RBSCloudManager.exec create cloud object service call executed")

                val res = kotlin.runCatching {
                    RBSCloudServiceImp.exec(
                        accessToken,
                        action,
                        RBSServiceParam(options)
                    )
                }

                if (res.isSuccess) {
                    RBSLogger.log("RBSCloudManager.exec success")

                    val result = res.getOrNull()?.body()?.string()

                    RBSLogger.log("RBSCloudManager.exec result: $result")

                    val instanceRes = Gson().fromJson(result, RBSInstanceResponse::class.java)

                    cloudObjects.find { it.params.instanceId == instanceRes.instanceId }
                        ?: kotlin.run {
                            RBSCloudObject(
                                RBSCloudObjectParams(
                                    options.classId!!,
                                    instanceRes.instanceId
                                )
                            ).apply {
                                cloudObjects.add(this)
                            }
                        }
                } else {
                    RBSLogger.log(
                        "RBSCloudManager.exec fail ${
                            res.exceptionOrNull()?.stackTraceToString()
                        }"
                    )

                    throw res.exceptionOrNull() ?: IllegalAccessError("RBSCloudManager.exec fail")
                }
            }
        }
    }

    fun clear() {
        cloudObjects.forEach { it.unsubscribeStates() }
        cloudObjects.clear()
    }
}