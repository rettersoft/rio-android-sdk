package com.rettermobile.rio.cloud

import com.rettermobile.rio.RioConfig
import com.rettermobile.rio.util.RioHttpMethod

/**
 * Created by semihozkoroglu on 19.12.2021.
 */
class RioServiceParam {

    var classId: String? = null

    // Key ve instanceId birlikte olamaz!
    var instanceId: String? = null
    var key: Pair<String, String>? = null
    var method: String? = null
    var httpMethod: RioHttpMethod = RioHttpMethod.POST
    var body: Any? = null
    var headers: Map<String, String> = mapOf()
    var queries: Map<String, String> = mapOf()

    var path = ""
    var culture: String

    constructor(objectOptions: RioGetCloudObjectOptions) {
        // GET INSTANCE
        instanceId = objectOptions.instanceId
        classId = objectOptions.classId
        key = objectOptions.key
        method = objectOptions.method
        httpMethod = objectOptions.httpMethod
        body = objectOptions.body
        headers = objectOptions.headers
        queries = objectOptions.queries
        culture = objectOptions.culture ?: RioConfig.culture

        path += if (!objectOptions.instanceId.isNullOrEmpty()) {
            objectOptions.instanceId
        } else if (objectOptions.key != null) {
            "${objectOptions.key!!.first}!${objectOptions.key!!.second}"
        } else {
            ""
        }
    }

    constructor(objectParams: RioCloudObjectParams, options: RioCallMethodOptions) {
        // CALL
        instanceId = objectParams.instanceId
        classId = objectParams.classId
        method = options.method
        httpMethod = options.httpMethod
        body = options.body
        headers = options.headers
        queries = options.queries
        culture = options.culture ?: RioConfig.culture

        path += if (!objectParams.instanceId.isNullOrEmpty()) {
            "${options.method}/${objectParams.instanceId}"
        } else {
            "${options.method}"
        }
    }
}