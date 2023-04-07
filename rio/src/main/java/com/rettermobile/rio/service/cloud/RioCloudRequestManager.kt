package com.rettermobile.rio.service.cloud

import com.google.gson.Gson
import com.rettermobile.rio.RioLogger
import com.rettermobile.rio.cloud.RioCloudObject
import com.rettermobile.rio.cloud.RioCloudObjectOptions
import com.rettermobile.rio.cloud.RioErrorResponse
import com.rettermobile.rio.cloud.RioServiceParam
import com.rettermobile.rio.service.model.RioInstanceResponse
import com.rettermobile.rio.util.RioActions
import com.rettermobile.rio.util.TokenManager

/**
 * Created by semihozkoroglu on 10.12.2021.
 */
internal object RioCloudRequestManager {

    suspend fun exec(
        action: RioActions,
        options: RioCloudObjectOptions
    ): RioCloudObject {
        TokenManager.checkToken()

        return if (options.useLocal && !options.instanceId.isNullOrEmpty()) {
            RioLogger.log("RIOCloudManager.exec create cloud object in-memory")
            RioCloudObject(options, null)
        } else {
            RioLogger.log("RIOCloudManager.exec create cloud object service call executed")

            val res = runCatching {
                RioCloudServiceImp.exec(
                    action,
                    RioServiceParam(options)
                )
            }

            if (res.isSuccess) {
                res.getOrNull()?.let {
                    it.body()?.string()?.let { result ->
                        RioLogger.log("RIOCloudManager.exec success")

                        RioLogger.log("RIOCloudManager.exec result: $result")

                        val instanceRes = Gson().fromJson(result, RioInstanceResponse::class.java)

                        RioCloudObject(
                            RioCloudObjectOptions(
                                classId = options.classId!!,
                                instanceId = instanceRes.instanceId
                            ),
                            instanceRes
                        )
                    } ?: it.errorBody()?.string()?.let { error ->
                        val exception = RioErrorResponse(it.headers(), it.code(), error)

                        RioLogger.log(
                            "RIOCloudManager.exec fail ${
                                exception.message ?: res.exceptionOrNull()?.stackTraceToString()
                            }"
                        )

                        throw exception
                    } ?: kotlin.run {
                        RioLogger.log(
                            "RIOCloudManager.exec fail ${
                                res.exceptionOrNull()?.stackTraceToString()
                            }"
                        )

                        throw res.exceptionOrNull() ?: IllegalAccessError("RIOCloudManager.exec fail")
                    }
                } ?: kotlin.run {
                    RioLogger.log(
                        "RIOCloudManager.exec fail ${
                            res.exceptionOrNull()?.stackTraceToString()
                        }"
                    )

                    throw res.exceptionOrNull() ?: IllegalAccessError("RIOCloudManager.exec fail")
                }
            } else {
                RioLogger.log(
                    "RIOCloudManager.exec fail ${
                        res.exceptionOrNull()?.stackTraceToString()
                    }"
                )

                throw res.exceptionOrNull() ?: IllegalAccessError("RIOCloudManager.exec fail")
            }
        }
    }
}