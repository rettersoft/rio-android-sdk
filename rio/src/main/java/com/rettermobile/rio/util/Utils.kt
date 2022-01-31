package com.rettermobile.rio.util

import android.app.ActivityManager
import android.text.TextUtils
import android.util.Base64
import com.auth0.android.jwt.JWT
import com.google.gson.Gson

/**
 * Created by semihozkoroglu on 15.02.2021.
 */
fun String.getBase64EncodeString(): String {
    if (TextUtils.isEmpty(this)) {
        return ""
    }

    val encodeValue = Base64.encode(this.toByteArray(), Base64.DEFAULT)

    return String(encodeValue)
}

fun isForegrounded(): Boolean {
    val appProcessInfo = ActivityManager.RunningAppProcessInfo()
    ActivityManager.getMyMemoryState(appProcessInfo)
    return (appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND || appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE)
}

infix fun <T> Boolean.then(param: T): T? = if (this) param else null

fun <T> parseResponse(clazz: Class<T>, json: String?): T? {
    if (json.isNullOrEmpty()) return null

    return Gson().fromJson(json, clazz)
}

fun String.jwtIat(): Long? {
    val jwtAccess = JWT(this)

    return jwtAccess.getClaim("iat").asLong()
}

fun String.jwtUserId(): String? {
    val jwtAccess = JWT(this)

    return jwtAccess.getClaim("userId").asString()
}

fun String.jwtIdentity(): String? {
    val jwtAccess = JWT(this)

    return jwtAccess.getClaim("identity").asString()
}

fun String.jwtAnonymous(): Boolean? {
    val jwtAccess = JWT(this)

    return jwtAccess.getClaim("anonymous").asBoolean()
}