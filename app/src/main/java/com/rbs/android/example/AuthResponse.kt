package com.rbs.android.example

import java.io.Serializable

/**
 * Created by semihozkoroglu on 25.11.2020.
 */
class AuthResponse : Serializable {

    var status: Int = 0
    var serviceId: String = ""
    var response: AuthModel? = null
}