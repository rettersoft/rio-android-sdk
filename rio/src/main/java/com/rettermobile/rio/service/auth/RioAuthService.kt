package com.rettermobile.rio.service.auth

import com.rettermobile.rio.service.model.AuthRequest
import com.rettermobile.rio.service.model.RefreshTokenRequest
import com.rettermobile.rio.service.model.RioTokenModel
import okhttp3.ResponseBody
import retrofit2.http.*

/**
 * Created by semihozkoroglu on 22.11.2020.
 */
interface RioAuthService {

    @POST
    suspend fun refresh(
        @Url url: String,
        @Body request: RefreshTokenRequest,
        @Query("__culture") culture: String,
        @Query("__platform") platform: String = "Android"
    ): RioTokenModel

    @POST
    suspend fun auth(
        @Url url: String,
        @Body request: AuthRequest,
        @Query("__culture") culture: String,
        @Query("__platform") platform: String = "Android"
    ): RioTokenModel

    @POST
    suspend fun signOut(
        @Url url: String,
        @Query("__culture") culture: String,
        @Query("__platform") platform: String = "Android"
    ): ResponseBody
}