package com.rettermobile.rbs.service.model

import java.io.Serializable

/**
 * Created by semihozkoroglu on 22.11.2020.
 */
class RBSTokenResponse : RBSResponse() {
    val response: RBSTokenModel? = null
}

class RBSTokenModel : RBSResponse() {
    val accessToken: String = ""
    val refreshToken: String = ""
    val firebase: RBSFirebase? = null
}

class RBSFirebase : Serializable {
    val customToken: String? = null
    val projectId: String? = null
    val apiKey: String? = null
    val envs: RBSFirebaseEnv? = null
}

class RBSFirebaseEnv : Serializable {
    val androidAppId: String? = null
    val gcmSenderId: String? = null
}