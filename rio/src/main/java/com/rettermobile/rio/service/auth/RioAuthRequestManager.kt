package com.rettermobile.rio.service.auth

import com.google.gson.Gson
import com.rettermobile.rio.RioLogger
import com.rettermobile.rio.cloud.RioErrorResponse
import com.rettermobile.rio.service.model.RioSignOutErrorBody
import com.rettermobile.rio.util.TokenManager
import okhttp3.Headers
import retrofit2.HttpException

/**
 * Created by semihozkoroglu on 10.12.2021.
 */
object RioAuthRequestManager {

    /**
     * Reported as the status of a business error that arrived inside a
     * successful response.
     *
     * [RioAuthService.signOut] returns a plain body, so this path has no access
     * to the status line or the headers - only to the payload. The signal the
     * caller needs is in `rawBody`, which carries the business error code; the
     * status and headers are filled in with what is known.
     */
    private const val HTTP_OK = 200

    private val gson = Gson()

    // Call with runCatching
    suspend fun authenticate(customToken: String) {
        TokenManager.authenticate(customToken)

        RioLogger.log("RIOAuthRequestManager.authenticate OK")
    }

    /**
     * Sends the signOut request and reports what actually happened.
     *
     * Until 1.9.1 this swallowed every outcome and returned "OK", so transport
     * failures, non-2xx responses and business errors returned inside a 2xx
     * response all surfaced as a successful sign out. It now throws instead;
     * whether the caller sees that failure is decided by
     * [com.rettermobile.rio.service.RioNetworkConfig.strictSignOutResult].
     *
     * Call with runCatching.
     */
    suspend fun signOut(type: String): String {
        val rawBody = try {
            RioAuthServiceImp.signOut(type).string()
        } catch (e: HttpException) {
            // Retrofit turns a non-2xx status into an exception before the body
            // ever reaches us, so the status and the error payload are read back
            // off the exception.
            val errorBody = runCatching { e.response()?.errorBody()?.string() }.getOrNull()

            RioLogger.log("RIOAuthRequestManager.signOut FAILED http: ${e.code()} body: $errorBody")

            throw e.response()
                ?.let { RioErrorResponse(it.headers(), it.code(), errorBody) }
                ?: e
        }

        businessErrorCode(rawBody)?.let { code ->
            RioLogger.log("RIOAuthRequestManager.signOut FAILED business error: $code body: $rawBody")

            throw RioErrorResponse(Headers.headersOf(), HTTP_OK, rawBody)
        }

        RioLogger.log("RIOAuthRequestManager.signOut OK")

        return "OK"
    }

    /**
     * A 2xx signOut response that carries a non-blank `code` is a business
     * error, not a successful sign out. Anything that is not a JSON object -
     * an empty body, a plain string - is treated as success.
     */
    private fun businessErrorCode(rawBody: String?): String? {
        if (rawBody.isNullOrBlank()) return null

        return runCatching {
            gson.fromJson(rawBody, RioSignOutErrorBody::class.java)?.code?.takeIf { it.isNotBlank() }
        }.getOrNull()
    }
}
