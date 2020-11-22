package com.rettermobile.rbs.service

import com.rettermobile.rbs.service.model.RBSTokenResponse
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Created by semihozkoroglu on 22.11.2020.
 */
interface RBSService {

    @GET("public/anonymous-auth")
    suspend fun anonymousAuth(
        @Query("projectId") projectId: String,
        @Query("clientId") clientId: String
    ): RBSTokenResponse

    @GET("public/auth-refresh")
    suspend fun authRefresh(@Query("refreshToken") refreshToken: String): RBSTokenResponse

    @GET("public/auth")
    suspend fun auth(@Query("customToken") customToken: String): RBSTokenResponse

    @POST("user/action")
    suspend fun action(@Query("auth") auth: String, @Query("action") action: String, @Body params: RequestBody): ResponseBody
}