package com.rettermobile.rio.cloud

import com.rettermobile.rio.util.RioHttpMethod

/**
 * Created by semihozkoroglu on 13.12.2021.
 */
class RioCallMethodOptions constructor(
    var method: String? = null,
    var httpMethod: RioHttpMethod = RioHttpMethod.POST,
    var body: Any? = null,
    var headers: Map<String, String> = mapOf(),
    var queries: Map<String, String> = mapOf()
)