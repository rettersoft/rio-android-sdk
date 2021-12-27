package com.rbs.android.example

import com.google.gson.Gson
import com.rettermobile.rbs.RBS
import com.rettermobile.rbs.RBSLogger

/**
 * Created by semihozkoroglu on 27.12.2021.
 */
class UserRepository constructor(rbs: RBS) {

    //

    init {
        rbs.setOnClientAuthStatusChangeListener { rbsClientAuthStatus, rbsUser ->
            RBSLogger.log(
                "rbsClientAuthStatus: $rbsClientAuthStatus rbsUser: ${
                    Gson().toJson(
                        rbsUser
                    )
                }"
            )
        }
    }
}