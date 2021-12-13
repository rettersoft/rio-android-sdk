package com.rettermobile.rbs.cloud

import com.rettermobile.rbs.util.RBSHttpMethod

/**
 * Created by semihozkoroglu on 13.12.2021.
 */
class RBSCloudObjectOptions constructor(
    var classId: String? = null,
    var method: String? = null,
    var instanceId: String? = null,
    var httpMethod: RBSHttpMethod? = RBSHttpMethod.POST,
    var payload: Map<String, String> = mapOf(),
    var headers: Map<String, String> = mapOf(),
    var queries: Map<String, String> = mapOf(),
    var key: Pair<String, String>? = null
)