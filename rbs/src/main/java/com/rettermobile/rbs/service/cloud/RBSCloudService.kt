package com.rettermobile.rbs.service.cloud

import com.rettermobile.rbs.service.model.RBSTokenResponse
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

/**
 * Created by semihozkoroglu on 22.11.2020.
 */
interface RBSCloudService {

    @GET
    suspend fun getAction(
        @Url url: String,
        @HeaderMap headers: Map<String, String>,
        @Query("_token") token: String?,
        @QueryMap queries: Map<String, String>
    ): Response<ResponseBody>

    @DELETE
    suspend fun deleteAction(
        @Url url: String,
        @HeaderMap headers: Map<String, String>,
        @Query("_token") token: String?,
        @QueryMap queries: Map<String, String>,
        @Body payload: RequestBody
    ): Response<ResponseBody>

    @PUT
    suspend fun putAction(
        @Url url: String,
        @HeaderMap headers: Map<String, String>,
        @Query("_token") token: String?,
        @QueryMap queries: Map<String, String>,
        @Body payload: RequestBody
    ): Response<ResponseBody>

    @POST
    suspend fun postAction(
        @Url url: String,
        @HeaderMap headers: Map<String, String>,
        @Query("_token") token: String?,
        @QueryMap queries: Map<String, String>,
        @Body payload: RequestBody
    ): Response<ResponseBody>
}