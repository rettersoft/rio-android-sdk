package com.rettermobile.rbs.cloud

import com.google.gson.Gson
import com.rettermobile.rbs.RBSLogger
import com.rettermobile.rbs.service.RBSCloudServiceImp
import com.rettermobile.rbs.service.model.RBSInstanceResponse
import com.rettermobile.rbs.util.RBSActions
import com.rettermobile.rbs.util.TokenManager
import okhttp3.ResponseBody
import retrofit2.Response

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

        val accessToken = TokenManager.accessToken

        val res = kotlin.runCatching {
            RBSCloudServiceImp.exec(
                accessToken,
                action,
                RBSServiceParam(options)
            )
        }

        return if (res.isSuccess) {
            RBSLogger.log("RBSCloudManager.exec success")

            val result = res.getOrNull()?.body()?.string()

            RBSLogger.log("RBSCloudManager.exec result: $result")

            val instanceRes = Gson().fromJson(result, RBSInstanceResponse::class.java)

            cloudObjects.find { it.params.instanceId == instanceRes.instanceId } ?: kotlin.run {
                RBSCloudObject(
                    RBSCloudObjectParams(
                        options.classId!!,
                        instanceRes.instanceId,
                        options.key
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

    suspend fun call(
        objectParams: RBSCloudObjectParams,
        action: RBSActions,
        options: RBSCallMethodOptions
    ): Response<ResponseBody> {
        TokenManager.checkToken()

        val accessToken = TokenManager.accessToken

        val res = kotlin.runCatching {
            RBSCloudServiceImp.exec(
                accessToken,
                action,
                RBSServiceParam(objectParams, options)
            )
        }

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

    fun clear() {
        cloudObjects.forEach { it.unsubscribeStates() }
        cloudObjects.clear()
    }
}