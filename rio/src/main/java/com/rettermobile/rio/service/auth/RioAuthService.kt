package com.rettermobile.rio.service.auth

import com.rettermobile.rio.service.model.RioTokenModel
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

/**
 * Created by semihozkoroglu on 22.11.2020.
 */
interface RioAuthService {

    // Refresh token:
    // [DOMAIN]/{projectId}/AUTH/refreshToken?refreshToken={refreshToken}
    @GET
    suspend fun refresh(@Url url: String, @Query("refreshToken") refreshToken: String, @Query("__culture") culture: String, @Query("__platform") platform: String = "Android"): RioTokenModel

    // Sign in with custom token:
    // [DOMAIN]/{projectId}/AUTH/authWithCustomToken?_token={accessToken}&customToken={customToken}
    @GET
    suspend fun auth(@Url url: String, @Query("customToken") customToken: String, @Query("__culture") culture: String, @Query("__platform") platform: String = "Android"): RioTokenModel

    // Sign out:
    // [DOMAIN]/{projectId}/AUTH/signOut?_token={accessToken}
    @GET
    suspend fun signOut(@Url url: String, @Query("_token") token: String?, @Query("__culture") culture: String, @Query("__platform") platform: String = "Android"): ResponseBody
}