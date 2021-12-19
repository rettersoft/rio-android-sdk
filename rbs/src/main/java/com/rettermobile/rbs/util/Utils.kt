package com.rettermobile.rbs.util

import android.app.ActivityManager
import android.text.TextUtils
import android.util.Base64
import com.auth0.android.jwt.JWT
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

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

inline fun <reified T> parseResponse(json: String?): T? {
    if (json.isNullOrEmpty()) return null

    val gson = Gson()
    val type = object : TypeToken<Map<String, Any>>() {}.type

    val response = gson.fromJson<Map<String, Any>>(json, type)

    return if (response.isNullOrEmpty()) null else gson.fromJson(
        gson.toJson(response),
        T::class.java
    )
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