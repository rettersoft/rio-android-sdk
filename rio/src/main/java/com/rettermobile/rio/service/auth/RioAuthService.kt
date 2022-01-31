package com.rettermobile.rio.service.auth

import com.rettermobile.rio.service.model.RioTokenModel
import com.rettermobile.rio.service.model.RioTokenResponse
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

/**
 * Created by semihozkoroglu on 22.11.2020.
 */
interface RioAuthService {

    // Anonym token:
    // https://root.api.rtbs.io/INSTANCE/ProjectUser?projectId={projectId}
    @GET
    suspend fun anonymous(@Url url: String, @Query("projectId") projectId: String): RioTokenResponse

    // Refresh token:
    // https://root.api.rtbs.io/CALL/ProjectUser/refreshToken/{projectId}_{userId}?refreshToken={refreshToken}
    @GET
    suspend fun refresh(@Url url: String, @Query("refreshToken") refreshToken: String): RioTokenModel

    // Sign in with custom token:
    // https://root.api.rtbs.io/CALL/ProjectUser/authWithCustomToken/{projectId}_{userId}?customToken={customToken}
    @GET
    suspend fun auth(@Url url: String, @Query("customToken") customToken: String): RioTokenModel

    // Sign out:
    // https://root.api.rtbs.io/CALL/ProjectUser/signOut/{projectId}_{userId}
    @GET
    suspend fun signOut(@Url url: String, @Query("accessToken") token: String?): ResponseBody
}