package com.rbs.android.example

import android.app.Application
import com.readystatesoftware.chuck.ChuckInterceptor
import com.rettermobile.rbs.RBS
import com.rettermobile.rbs.RBSNetworkConfig
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
            projectId = "69ec1ef0039b4332b3e102f082a98ec2",
            config = RBSNetworkConfig.build {
                region = RBSRegion.EU_WEST_1_BETA
                sslPinningEnabled = true
            }
        )
    }
}