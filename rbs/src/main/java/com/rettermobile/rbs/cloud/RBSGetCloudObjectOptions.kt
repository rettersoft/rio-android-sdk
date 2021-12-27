package com.rettermobile.rbs.cloud

import com.rettermobile.rbs.util.RBSHttpMethod

/**
 * Created by semihozkoroglu on 13.12.2021.
 */
class RBSGetCloudObjectOptions constructor(
    var classId: String? = null,
    // Key ve instanceId birlikte olamaz!
    var instanceId: String? = null,
    var key: Pair<String, String>? = null,
    var method: String? = null,
    var httpMethod: RBSHttpMethod = RBSHttpMethod.POST,
    var body: Any? = null,
    var headers: Map<String, String> = mapOf(),
    var queries: Map<String, String> = mapOf(),
    var useLocal: Boolean = false
)