package com.rettermobile.rio.cloud

import com.rettermobile.rio.util.RioHttpMethod
import java.lang.reflect.Type

/**
 * Created by semihozkoroglu on 13.12.2021.
 */
class RioCloudObjectOptions constructor(
    var classId: String? = null,
    // Key ve instanceId birlikte olamaz!
    var instanceId: String? = null,
    var key: Pair<String, String>? = null,
    var method: String? = null,
    var httpMethod: RioHttpMethod = RioHttpMethod.POST,
    var body: Any? = null,
    var headers: Map<String, String> = mapOf(),
    var queries: Map<String, Any> = mapOf(),
    var useLocal: Boolean = false,
    var path: String? = null,
    var culture: String? = null,
    var type: Type? = null
)