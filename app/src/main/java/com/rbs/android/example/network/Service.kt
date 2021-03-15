package com.rbs.android.example.network

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Url

/**
 * Created by semihozkoroglu on 22.11.2020.
 */
interface Service {

    @GET
    suspend fun get(@Url url: String): ResponseBody
}