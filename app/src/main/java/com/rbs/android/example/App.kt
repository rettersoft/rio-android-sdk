package com.rbs.android.example

import android.app.Application
import com.google.gson.GsonBuilder
import com.rettermobile.rio.Rio
import com.rettermobile.rio.service.RioNetworkConfig

/**
 * Created by semihozkoroglu on 7.08.2021.
 */
class App : Application() {

    lateinit var rio: Rio

    override fun onCreate() {
        super.onCreate()

        rio = Rio(
            applicationContext = applicationContext,
//            projectId = "6qub7mnar",
            projectId = "43dl208sr",
            culture = "tr-tr",
            config = RioNetworkConfig.build {
                customDomain = "api.riotestv1.retter.io"
                sslPinningEnabled = true
                gson = GsonBuilder().create()
            },
        )
    }
}