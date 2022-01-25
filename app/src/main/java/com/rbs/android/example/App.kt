package com.rbs.android.example

import android.app.Application
import com.readystatesoftware.chuck.ChuckInterceptor
import com.rettermobile.rbs.RBS
import com.rettermobile.rbs.service.RBSNetworkConfig
import com.rettermobile.rbs.util.RBSRegion

/**
 * Created by semihozkoroglu on 7.08.2021.
 */
class App : Application() {

    lateinit var rbs: RBS

    override fun onCreate() {
        super.onCreate()

        rbs = RBS(
            applicationContext = applicationContext,
            projectId = "11c5e84qtq",
            config = RBSNetworkConfig.build {
                region = RBSRegion.EU_WEST_1
                sslPinningEnabled = true
            }
        )
    }
}