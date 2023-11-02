package com.rettermobile.rio.service.cloud

import com.google.gson.Gson
import com.rettermobile.rio.RioConfig
import com.rettermobile.rio.RioLogger
import com.rettermobile.rio.cloud.RioServiceParam
import com.rettermobile.rio.service.RioNetwork
import com.rettermobile.rio.util.RioActions
import com.rettermobile.rio.util.RioHttpMethod
import com.rettermobile.rio.util.TokenManager
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Response

/**
 * Created by semihozkoroglu on 22.11.2020.
 */
object RioCloudServiceImp {

    private var api: RioCloudService = RioNetwork().getCloudConnection()

    suspend fun exec(
        action: RioActions,
        params: RioServiceParam
    ): Response<ResponseBody> {
        RioLogger.log("getCloud $action started")

        RioLogger.log("RIOCloudManager.exec projectId: ${RioConfig.projectId}")
        RioLogger.log("RIOCloudManager.exec accessToken: ${TokenManager.accessToken()}")
        RioLogger.log("RIOCloudManager.exec action: $action")
        RioLogger.log("RIOCloudManager.exec classId: ${params.classId}")
        RioLogger.log("RIOCloudManager.exec methodId: ${params.method}")
        RioLogger.log("RIOCloudManager.exec instanceId: ${params.instanceId}")
        RioLogger.log("RIOCloudManager.exec headers: ${Gson().toJson(params.headers)}")
        RioLogger.log("RIOCloudManager.exec queries: ${Gson().toJson(params.query)}")
        RioLogger.log("RIOCloudManager.exec body: ${Gson().toJson(params.body)}")

        val body: RequestBody = if (params.body == null) {
            "".toRequestBody("application/json; charset=utf-8".toMediaType())
        } else {
            Gson().toJson(params.body)
                .toRequestBody("application/json; charset=utf-8".toMediaType())
        }

        /**
         * { method}/{classId}/{path1}/{path2}
         */
        val url = RioConfig.projectId + if (params.path.isEmpty()) {
            if (params.query.isNullOrEmpty()) {
                "/${action.name}/${params.classId}"
            } else {
                "/${action.name}/${params.classId}/${params.query}"
            }
        } else {
            if (params.query.isNullOrEmpty()) {
                "/${action.name}/${params.classId}/${params.path}"
            } else {
                "/${action.name}/${params.classId}/${params.path}/${params.query}"
            }
        }

        return when (params.httpMethod) {
            RioHttpMethod.GET -> api.getAction(url = url, culture = params.culture, headers = params.headers)
            RioHttpMethod.POST -> api.postAction(url = url, culture = params.culture, headers = params.headers, payload = body)
            RioHttpMethod.DELETE -> api.deleteAction(url = url, culture = params.culture, headers = params.headers)
            RioHttpMethod.PUT -> api.putAction(url = url, culture = params.culture, headers = params.headers, payload = body)
        }
    }
}