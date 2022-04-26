package com.rettermobile.rio.cloud

import com.rettermobile.rio.util.parseResponse
import okhttp3.Headers
import java.lang.reflect.Type

/**
 * Created by semihozkoroglu on 19.12.2021.
 */
class RioErrorResponse constructor(val headers: Headers, val code: Int, val rawBody: String?) : Throwable(rawBody) {

    inline fun <reified T> body() = parseResponse<T>(T::class.java, rawBody)
}