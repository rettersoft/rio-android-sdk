package com.rettermobile.rbs.service

import com.google.gson.Gson
import com.rettermobile.rbs.RBSConfig
import com.rettermobile.rbs.RBSLogger
import com.rettermobile.rbs.cloud.RBSCloudObjectOptions
import com.rettermobile.rbs.util.RBSActions
import com.rettermobile.rbs.util.getBase64EncodeString
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody

/**
 * Created by semihozkoroglu on 22.11.2020.
 */
object RBSCloudServiceImp {

    private var cloudApi: RBSCloudService = RBSNetwork().getCloudConnection()

    suspend fun exec(
        accessToken: String? = null,
        action: RBSActions,
        options: RBSCloudObjectOptions
    ): Result<ResponseBody?> {
        RBSLogger.log("getCloud $action started")

        return kotlin.runCatching {
            var path1 = ""
            var path2 = ""

            if (action == RBSActions.CALL) {
                path1 += "${options.method}"
                path2 += if (options.key != null) {
                    "${options.key!!.first}!${options.key!!.second}"
                } else if (!options.instanceId.isNullOrEmpty()) {
                    "${options.instanceId}"
                } else {
                    ""
                }
            } else {
                path1 += if (options.key != null) {
                    "${options.key!!.first}!${options.key!!.second}"
                } else if (!options.instanceId.isNullOrEmpty()) {
                    "${options.instanceId}"
                } else {
                    ""
                }
            }

            val requestEncodedJsonString = if (options.payload.isEmpty()) {
                null
            } else {
                Gson().toJson(options.payload).getBase64EncodeString()
            }

            RBSLogger.log("RBSCloudManager.exec projectId: ${RBSConfig.projectId}")
            RBSLogger.log("RBSCloudManager.exec accessToken: $accessToken")
            RBSLogger.log("RBSCloudManager.exec action: $action")
            RBSLogger.log("RBSCloudManager.exec classId: ${options.classId}")
            RBSLogger.log("RBSCloudManager.exec methodId: ${options.method}")
            RBSLogger.log("RBSCloudManager.exec instanceId: ${options.instanceId}")
            RBSLogger.log("RBSCloudManager.exec headers: ${Gson().toJson(options.headers)}")
            RBSLogger.log("RBSCloudManager.exec queries: ${Gson().toJson(options.queries)}")
            RBSLogger.log("RBSCloudManager.exec body: ${Gson().toJson(options.payload)}")
            RBSLogger.log("RBSCloudManager.exec bodyEncodeString: $requestEncodedJsonString")

            val body: RequestBody = if (requestEncodedJsonString.isNullOrEmpty()) {
                "".toRequestBody("application/json; charset=utf-8".toMediaType())
            } else {
                requestEncodedJsonString.toRequestBody("application/json; charset=utf-8".toMediaType())
            }

            cloudApi.postAction(
                token = accessToken,
                method = action.name,
                classId = options.classId!!,
                path1 = path1,
                path2 = path2,
                headers = options.headers,
                queries = options.queries,
                payload = body
            )
        }
    }
}