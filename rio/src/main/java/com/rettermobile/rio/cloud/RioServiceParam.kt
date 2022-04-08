package com.rettermobile.rio.cloud

import com.google.gson.Gson
import com.rettermobile.rio.RioConfig
import com.rettermobile.rio.util.RioHttpMethod
import com.rettermobile.rio.util.getBase64EncodeString

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
    var query: String? = null

    var path = ""
    var culture: String

    constructor(objectOptions: RioCloudObjectOptions) {
        // GET INSTANCE
        instanceId = objectOptions.instanceId
        classId = objectOptions.classId
        key = objectOptions.key
        method = objectOptions.method
        httpMethod = objectOptions.httpMethod
        body = objectOptions.body
        headers = objectOptions.headers

        if (objectOptions.queries.isNotEmpty()) {
            query = "?"

            objectOptions.queries.forEach { (key, value) ->
                when (value) {
                    is Array<*> -> {
                        value.forEach { query += "$key=${it.toString()}&" }
                    }
                    is List<*> -> {
                        value.forEach { query += "$key=${it.toString()}&" }
                    }
                    else -> {
                        query += "$key=$value&"
                    }
                }
            }

            query = query?.substring(0, query!!.length - 1)
        }

        culture = objectOptions.culture ?: RioConfig.culture

        path += if (!objectOptions.instanceId.isNullOrEmpty()) {
            objectOptions.instanceId
        } else if (objectOptions.key != null) {
            "${objectOptions.key!!.first}!${objectOptions.key!!.second}"
        } else {
            ""
        }
    }

    constructor(cloudOptions: RioCloudObjectOptions, callOptions: RioCallMethodOptions) {
        // CALL & LIST
        instanceId = cloudOptions.instanceId
        classId = cloudOptions.classId
        method = callOptions.method
        httpMethod = callOptions.httpMethod
        body = callOptions.body
        headers = callOptions.headers

        if (callOptions.queries.isNotEmpty()) {
            query = "?"

            callOptions.queries.forEach { (key, value) ->
                when (value) {
                    is Array<*> -> {
                        value.forEach { query += "$key=${it.toString()}&" }
                    }
                    is List<*> -> {
                        value.forEach { query += "$key=${it.toString()}&" }
                    }
                    else -> {
                        query += "$key=$value&"
                    }
                }
            }

            query = query?.substring(0, query!!.length - 1)
        }

        if (httpMethod == RioHttpMethod.GET) {
            query += if (body != null) {
                val requestJsonString = Gson().toJson(body)
                val requestJsonStringEncoded = requestJsonString.getBase64EncodeString()
                "__isbase64=true&data=$requestJsonStringEncoded"
            } else {
                "__isbase64=false"
            }
        }

        culture = callOptions.culture ?: RioConfig.culture

        path += if (!cloudOptions.instanceId.isNullOrEmpty()) {
            "${callOptions.method}/${cloudOptions.instanceId}"
        } else if (!callOptions.method.isNullOrEmpty()) {
            "${callOptions.method}"
        } else {
            ""
        }
    }
}