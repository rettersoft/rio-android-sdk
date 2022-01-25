package com.rettermobile.rbs.cloud

import com.rettermobile.rbs.util.parseResponse
import okhttp3.Headers
import okhttp3.ResponseBody
import retrofit2.Response

/**
 * Created by semihozkoroglu on 19.12.2021.
 */
class RBSCloudSuccessResponse<T> constructor(val headers: Headers, val code: Int, val body: T?)