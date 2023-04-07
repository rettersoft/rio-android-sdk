package com.rettermobile.rio.service.cloud

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

/**
 * Created by semihozkoroglu on 22.11.2020.
 */
interface RioCloudService {

    @GET
    suspend fun getAction(
        @Url url: String,
        @HeaderMap headers: Map<String, String>,
        @Query("__culture") culture: String,
        @Query("__platform") platform: String = "Android"
    ): Response<ResponseBody>

    @DELETE
    suspend fun deleteAction(
        @Url url: String,
        @HeaderMap headers: Map<String, String>,
        @Query("__culture") culture: String,
        @Query("__platform") platform: String = "Android",
        @Body payload: RequestBody
    ): Response<ResponseBody>

    @PUT
    suspend fun putAction(
        @Url url: String,
        @HeaderMap headers: Map<String, String>,
        @Query("__culture") culture: String,
        @Query("__platform") platform: String = "Android",
        @Body payload: RequestBody
    ): Response<ResponseBody>

    @POST
    suspend fun postAction(
        @Url url: String,
        @HeaderMap headers: Map<String, String>,
        @Query("__culture") culture: String,
        @Query("__platform") platform: String = "Android",
        @Body payload: RequestBody
    ): Response<ResponseBody>
}