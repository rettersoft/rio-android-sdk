package com.rettermobile.rbs.service

import com.google.gson.Gson
import com.rettermobile.rbs.RBSConfig
import com.rettermobile.rbs.RBSLogger
import com.rettermobile.rbs.cloud.RBSServiceParam
import com.rettermobile.rbs.util.RBSActions
import com.rettermobile.rbs.util.RBSHttpMethod
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Response

/**
 * Created by semihozkoroglu on 22.11.2020.
 */
object RBSCloudServiceImp {

    private var cloudApi: RBSCloudService = RBSNetwork().getCloudConnection()

    suspend fun exec(
        accessToken: String? = null,
        action: RBSActions,
        params: RBSServiceParam
    ): Response<ResponseBody> {
        RBSLogger.log("getCloud $action started")

        RBSLogger.log("RBSCloudManager.exec projectId: ${RBSConfig.projectId}")
        RBSLogger.log("RBSCloudManager.exec accessToken: $accessToken")
        RBSLogger.log("RBSCloudManager.exec action: $action")
        RBSLogger.log("RBSCloudManager.exec classId: ${params.classId}")
        RBSLogger.log("RBSCloudManager.exec methodId: ${params.method}")
        RBSLogger.log("RBSCloudManager.exec instanceId: ${params.instanceId}")
        RBSLogger.log("RBSCloudManager.exec headers: ${Gson().toJson(params.headers)}")
        RBSLogger.log("RBSCloudManager.exec queries: ${Gson().toJson(params.queries)}")
        RBSLogger.log("RBSCloudManager.exec body: ${Gson().toJson(params.body)}")

        val body: RequestBody = if (params.body == null) {
            "".toRequestBody("application/json; charset=utf-8".toMediaType())
        } else {
            Gson().toJson(params.body)
                .toRequestBody("application/json; charset=utf-8".toMediaType())
        }

        return when (params.httpMethod) {
            RBSHttpMethod.GET -> cloudApi.getAction(
                token = accessToken,
                method = action.name,
                classId = params.classId!!,
                path1 = params.path1,
                path2 = params.path2,
                headers = params.headers,
                queries = params.queries
            )
            RBSHttpMethod.POST -> cloudApi.postAction(
                token = accessToken,
                method = action.name,
                classId = params.classId!!,
                path1 = params.path1,
                path2 = params.path2,
                headers = params.headers,
                queries = params.queries,
                payload = body
            )
            RBSHttpMethod.DELETE -> cloudApi.deleteAction(
                token = accessToken,
                method = action.name,
                classId = params.classId!!,
                path1 = params.path1,
                path2 = params.path2,
                headers = params.headers,
                queries = params.queries,
                payload = body
            )
            RBSHttpMethod.PUT -> cloudApi.putAction(
                token = accessToken,
                method = action.name,
                classId = params.classId!!,
                path1 = params.path1,
                path2 = params.path2,
                headers = params.headers,
                queries = params.queries,
                payload = body
            )
        }
    }
}