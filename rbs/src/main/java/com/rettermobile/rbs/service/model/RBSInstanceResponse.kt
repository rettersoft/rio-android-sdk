package com.rettermobile.rbs.service.model

import java.io.Serializable

/**
 * Created by semihozkoroglu on 22.11.2020.
 */
open class RBSInstanceResponse : Serializable {
    var instanceId: String = ""
    val newInstance: Boolean = false
    val methods: List<RBSInstanceMethod>? = null
}

open class RBSInstanceMethod : Serializable {
    val name: String? = null
    val tag: String? = null
    val readonly: Boolean? = null
    val sync: Boolean? = null
}