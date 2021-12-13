package com.rbs.android.example

import android.app.Application
import android.graphics.Color
import com.rettermobile.rbs.RBS
import com.rettermobile.rbs.util.Logger
import com.rettermobile.rbs.util.RBSRegion
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

/**
 * Created by semihozkoroglu on 7.08.2021.
 */
class App : Application() {

    lateinit var rbs: RBS

    override fun onCreate() {
        super.onCreate()

        rbs = RBS(
            applicationContext = applicationContext,
            projectId = "6eedd7ca16be4ae8982451fdfdba7e15",
            region = RBSRegion.EU_WEST_1_BETA
        )
    }
}