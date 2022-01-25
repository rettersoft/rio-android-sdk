package com.rettermobile.rbs.service.auth

import com.rettermobile.rbs.service.model.RBSTokenModel
import com.rettermobile.rbs.service.model.RBSTokenResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

/**
 * Created by semihozkoroglu on 22.11.2020.
 */
interface RBSAuthService {

    // Anonym token:
    // https://root.api.rtbs.io/INSTANCE/ProjectUser?projectId={projectId}
    @GET
    suspend fun anonymous(@Url url: String, @Query("projectId") projectId: String): RBSTokenResponse

    // Refresh token:
    // https://root.api.rtbs.io/CALL/ProjectUser/refreshToken/{projectId}_{userId}?refreshToken={refreshToken}
    @GET
    suspend fun refresh(@Url url: String, @Query("refreshToken") refreshToken: String): RBSTokenModel

    // Sign in with custom token:
    // https://root.api.rtbs.io/CALL/ProjectUser/authWithCustomToken/{projectId}_{userId}?customToken={customToken}
    @GET
    suspend fun auth(@Url url: String, @Query("customToken") customToken: String): RBSTokenModel

    // Sign out:
    // https://root.api.rtbs.io/CALL/ProjectUser/signOut/{projectId}_{userId}
    @GET
    suspend fun signOut(@Url url: String, @Query("accessToken") token: String?): ResponseBody
}