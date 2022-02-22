package com.rettermobile.rio.service.cloud

import com.google.gson.Gson
import com.rettermobile.rio.RioConfig
import com.rettermobile.rio.RioLogger
import com.rettermobile.rio.cloud.RioServiceParam
import com.rettermobile.rio.service.RioNetwork
import com.rettermobile.rio.util.RioActions
import com.rettermobile.rio.util.RioHttpMethod
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
        accessToken: String? = null,
        action: RioActions,
        params: RioServiceParam
    ): Response<ResponseBody> {
        RioLogger.log("getCloud $action started")

        RioLogger.log("RBSCloudManager.exec projectId: ${RioConfig.projectId}")
        RioLogger.log("RBSCloudManager.exec accessToken: $accessToken")
        RioLogger.log("RBSCloudManager.exec action: $action")
        RioLogger.log("RBSCloudManager.exec classId: ${params.classId}")
        RioLogger.log("RBSCloudManager.exec methodId: ${params.method}")
        RioLogger.log("RBSCloudManager.exec instanceId: ${params.instanceId}")
        RioLogger.log("RBSCloudManager.exec headers: ${Gson().toJson(params.headers)}")
        RioLogger.log("RBSCloudManager.exec queries: ${Gson().toJson(params.queries)}")
        RioLogger.log("RBSCloudManager.exec body: ${Gson().toJson(params.body)}")

        val body: RequestBody = if (params.body == null) {
            "".toRequestBody("application/json; charset=utf-8".toMediaType())
        } else {
            Gson().toJson(params.body)
                .toRequestBody("application/json; charset=utf-8".toMediaType())
        }

        /**
         * { method}/{classId}/{path1}/{path2}
         */
        val url = if (params.path.isEmpty()) {
            "${action.name}/${params.classId}"
        } else {
            "${action.name}/${params.classId}/${params.path}"
        }

        return when (params.httpMethod) {
            RioHttpMethod.GET -> api.getAction(url = url, token = accessToken, culture = params.culture, headers = params.headers, queries = params.queries)
            RioHttpMethod.POST -> api.postAction(url = url, token = accessToken, culture = params.culture, headers = params.headers, queries = params.queries, payload = body)
            RioHttpMethod.DELETE -> api.deleteAction(url = url, token = accessToken, culture = params.culture, headers = params.headers, queries = params.queries, payload = body)
            RioHttpMethod.PUT -> api.putAction(url = url, token = accessToken, culture = params.culture, headers = params.headers, queries = params.queries, payload = body)
        }
    }
}