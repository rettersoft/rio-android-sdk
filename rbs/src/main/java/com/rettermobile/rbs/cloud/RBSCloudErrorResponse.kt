package com.rettermobile.rbs.cloud

import com.rettermobile.rbs.util.parseResponse
import okhttp3.Headers
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response

/**
 * Created by semihozkoroglu on 19.12.2021.
 */
class RBSCloudErrorResponse constructor(val error: Throwable?) {
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