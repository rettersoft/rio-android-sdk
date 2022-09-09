package com.rettermobile.rio.util

import android.app.ActivityManager
import android.text.TextUtils
import android.util.Base64
import com.auth0.android.jwt.JWT
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.google.gson.Gson
import java.lang.reflect.Type

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

fun <T> parseResponse(type: Type, json: String?): T? {
    if (json.isNullOrEmpty()) return null

    return Gson().fromJson(json, type)
}

fun String.jwtIat(): Long? {
    val jwtAccess = JWT(this)

    return jwtAccess.getClaim("iat").asLong()
}

fun String.projectId(): String? {
    val jwtAccess = JWT(this)

    return jwtAccess.getClaim("projectId").asString()
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

fun String.sortAlphabetically(): String {
    return try {
        val mapper = ObjectMapper().apply {
            configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
            configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true)
        }

        val type = object : TypeReference<Map<String, Any>>() {}
        val res = mapper.readValue(this, type)

        mapper.writeValueAsString(res)
    } catch (e: Exception) {
        this
    }
}
