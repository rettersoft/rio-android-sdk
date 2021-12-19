package com.rettermobile.rbs.cloud

import com.rettermobile.rbs.util.RBSHttpMethod

/**
 * Created by semihozkoroglu on 19.12.2021.
 */
class RBSServiceParam {

    var classId: String? = null

    // Key ve instanceId birlikte olamaz!
    var instanceId: String? = null
    var key: Pair<String, String>? = null
    var method: String? = null
    var httpMethod: RBSHttpMethod = RBSHttpMethod.POST
    var body: Any? = null
    var headers: Map<String, String> = mapOf()
    var queries: Map<String, String> = mapOf()

    var path1 = ""
    var path2 = ""

    constructor(objectOptions: RBSGetCloudObjectOptions) {
        // GET INSTANCE

        instanceId = objectOptions.instanceId
        classId = objectOptions.classId
        key = objectOptions.key
        method = objectOptions.method
        httpMethod = objectOptions.httpMethod
        body = objectOptions.body
        headers = objectOptions.headers
        queries = objectOptions.queries

        path1 += if (objectOptions.key != null) {
            "${objectOptions.key!!.first}!${objectOptions.key!!.second}"
        } else if (!objectOptions.instanceId.isNullOrEmpty()) {
            "${objectOptions.instanceId}"
        } else {
            ""
        }
    }

    constructor(objectParams: RBSCloudObjectParams, options: RBSCallMethodOptions) {
        // CALL
        instanceId = objectParams.instanceId
        classId = objectParams.classId
        key = objectParams.key
        method = options.method
        httpMethod = options.httpMethod
        body = options.body
        headers = options.headers
        queries = options.queries

        path1 += "${options.method}"
        path2 += if (objectParams.key != null) {
            "${objectParams.key!!.first}!${objectParams.key!!.second}"
        } else if (!objectParams.instanceId.isNullOrEmpty()) {
            "${objectParams.instanceId}"
        } else {
            ""
        }
    }
}