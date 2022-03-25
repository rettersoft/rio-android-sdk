package com.rettermobile.rio.service.model

import java.io.Serializable

/**
 * Created by semihozkoroglu on 22.11.2020.
 */
open class RioInstanceResponse : Serializable {
    var instanceId: String = ""
    val newInstance: Boolean = false
    val methods: List<RioInstanceMethod>? = null
    val response: Map<String, Any>? = null
}

open class RioInstanceMethod : Serializable {
    val name: String? = null
    val tag: String? = null
    val readonly: Boolean? = null
    val sync: Boolean? = null
}