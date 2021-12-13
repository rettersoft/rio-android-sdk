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
        options: RBSCloudObjectOptions
    ): RBSCloudObject {
        TokenManager.checkToken()

        val accessToken = TokenManager.accessToken

        val res = RBSCloudServiceImp.exec(accessToken, action, options)

        return if (res.isSuccess) {
            RBSLogger.log("RBSCloudManager.exec success")

            val result = res.getOrNull()?.string()

            RBSLogger.log("RBSCloudManager.exec result: $result")

            val instanceRes = Gson().fromJson(result, RBSInstanceResponse::class.java)

            cloudObjects.find { it.instanceId == instanceRes.instanceId } ?: kotlin.run {
                RBSCloudObject(options.classId!!, instanceRes.instanceId).apply {
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

    suspend fun call(
        action: RBSActions,
        options: RBSCloudObjectOptions
    ): String {
        TokenManager.checkToken()

        val accessToken = TokenManager.accessToken

        val res = RBSCloudServiceImp.exec(accessToken, action, options)

        return if (res.isSuccess) {
            RBSLogger.log("RBSCloudManager.exec success")

            val result = res.getOrNull()?.string()

            result ?: ""
        } else {
            RBSLogger.log(
                "RBSCloudManager.exec fail ${
                    res.exceptionOrNull()?.stackTraceToString()
                }"
            )

            throw res.exceptionOrNull() ?: IllegalAccessError("RBSCloudManager.exec fail")
        }
    }

    fun clear() {
        cloudObjects.forEach { it.unsubscribeStates() }
        cloudObjects.clear()
    }
}