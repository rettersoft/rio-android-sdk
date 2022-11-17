package com.rettermobile.rio.cloud

import com.rettermobile.rio.service.RioRetryConfig
import com.rettermobile.rio.util.RioHttpMethod
import java.lang.reflect.Type

/**
 * Created by semihozkoroglu on 13.12.2021.
 */
class RioCallMethodOptions constructor(
    var method: String? = null,
    var httpMethod: RioHttpMethod = RioHttpMethod.POST,
    var body: Any? = null,
    var headers: Map<String, String> = mapOf(),
    var queries: Map<String, Any> = mapOf(),
    var culture: String? = null,
    var path: String? = null,
    var retry: RioRetryConfig? = null,
    var type: Type? = null,
)