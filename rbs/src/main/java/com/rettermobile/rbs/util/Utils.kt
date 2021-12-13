package com.rettermobile.rbs.util

import android.app.ActivityManager
import android.text.TextUtils
import android.util.Base64

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