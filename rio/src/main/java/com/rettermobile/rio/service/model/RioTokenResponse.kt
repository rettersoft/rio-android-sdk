package com.rettermobile.rio.service.model

import java.io.Serializable

/**
 * Created by semihozkoroglu on 22.11.2020.
 */
class RioTokenResponse : RioResponse() {
    val response: RioTokenModel? = null
}

class RioTokenModel : RioResponse() {
    val accessToken: String = ""
    val refreshToken: String = ""
    val firebase: RioFirebase? = null
}

class RioFirebase : Serializable {
    val customToken: String? = null
    val projectId: String? = null
    val apiKey: String? = null
    val envs: RioFirebaseEnv? = null
}

class RioFirebaseEnv : Serializable {
    val androidAppId: String? = null
    val gcmSenderId: String? = null
}