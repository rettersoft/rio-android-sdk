package com.rettermobile.rio.cloud

/**
 * Created by semihozkoroglu on 19.12.2021.
 */
class RioCloudErrorResponse constructor(val error: Throwable?) {
//    fun headers(): Headers? = response?.headers()
//    fun code(): Int? = response?.code()
//    inline fun <reified T> errorBody(): T? {
//        return if (error is HttpException) {
//            parseResponse<T>(response?.body()?.string())
//        } else {
//            null
//        }
//    }
}