package com.rettermobile.rbs.service

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

/**
 * Created by semihozkoroglu on 22.11.2020.
 */
interface RBSCloudService {

    @GET("{method}/{path}")
    suspend fun getAction(
        @HeaderMap headers: Map<String, String>,
        @Path("method") method: String,
        @Path("path") path: String,
        @Query("_token") token: String?,
        @QueryMap queries: Map<String, String>
    ): ResponseBody

    @DELETE("{method}/{path}")
    suspend fun deleteAction(
        @HeaderMap headers: Map<String, String>,
        @Path("method") method: String,
        @Path("path") path: String,
        @Query("_token") token: String?,
        @QueryMap queries: Map<String, String>,
        @Body payload: RequestBody,
    ): ResponseBody

    @PUT("{method}/{path}")
    suspend fun putAction(
        @HeaderMap headers: Map<String, String>,
        @Path("method") method: String,
        @Path("path") path: String,
        @Query("_token") token: String?,
        @QueryMap queries: Map<String, String>,
        @Body payload: RequestBody,
    ): ResponseBody

    @POST("{method}/{classId}/{path1}/{path2}")
    suspend fun postAction(
        @HeaderMap headers: Map<String, String>,
        @Path("method") method: String,
        @Path("classId") classId: String,
        @Path("path1") path1: String,
        @Path("path2") path2: String,
        @Query("_token") token: String?,
        @QueryMap queries: Map<String, String>,
        @Body payload: RequestBody? = null,
    ): ResponseBody
}