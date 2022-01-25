package com.rettermobile.rbs

import android.content.Context
import com.rettermobile.rbs.service.RBSNetworkConfig

/**
 * Created by semihozkoroglu on 10.12.2021.
 */
object RBSConfig {
    lateinit var applicationContext: Context
    lateinit var projectId: String
    var culture: String? = null

    var config = RBSNetworkConfig()
}