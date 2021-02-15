package com.rettermobile.rbs.util

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