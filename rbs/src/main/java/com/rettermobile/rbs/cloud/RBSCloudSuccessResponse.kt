package com.rettermobile.rbs.cloud

import com.rettermobile.rbs.util.parseResponse
import okhttp3.Headers
import okhttp3.ResponseBody
import retrofit2.Response

/**
 * Created by semihozkoroglu on 19.12.2021.
 */
class RBSCloudSuccessResponse<T> constructor(val clazz: Class<T>, val response: Response<ResponseBody>?) {
    fun headers(): Headers? = response?.headers()
    fun code(): Int? = response?.code()
    fun body(): T? = parseResponse(clazz, response?.body()?.string())
}