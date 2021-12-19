package com.rettermobile.rbs.service

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

/**
 * Created by semihozkoroglu on 22.11.2020.
 */
interface RBSCloudService {

    @GET("{method}/{classId}/{path1}/{path2}")
    suspend fun getAction(
        @HeaderMap headers: Map<String, String>,
        @Path("method") method: String,
        @Path("classId") classId: String,
        @Path("path1") path1: String,
        @Path("path2") path2: String,
        @Query("_token") token: String?,
        @QueryMap queries: Map<String, String>
    ): Response<ResponseBody>

    @DELETE("{method}/{classId}/{path1}/{path2}")
    suspend fun deleteAction(
        @HeaderMap headers: Map<String, String>,
        @Path("method") method: String,
        @Path("classId") classId: String,
        @Path("path1") path1: String,
        @Path("path2") path2: String,
        @Query("_token") token: String?,
        @QueryMap queries: Map<String, String>,
        @Body payload: RequestBody
    ): Response<ResponseBody>

    @PUT("{method}/{classId}/{path1}/{path2}")
    suspend fun putAction(
        @HeaderMap headers: Map<String, String>,
        @Path("method") method: String,
        @Path("classId") classId: String,
        @Path("path1") path1: String,
        @Path("path2") path2: String,
        @Query("_token") token: String?,
        @QueryMap queries: Map<String, String>,
        @Body payload: RequestBody
    ): Response<ResponseBody>

    @POST("{method}/{classId}/{path1}/{path2}")
    suspend fun postAction(
        @HeaderMap headers: Map<String, String>,
        @Path("method") method: String,
        @Path("classId") classId: String,
        @Path("path1") path1: String,
        @Path("path2") path2: String,
        @Query("_token") token: String?,
        @QueryMap queries: Map<String, String>,
        @Body payload: RequestBody
    ): Response<ResponseBody>
}