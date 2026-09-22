package com.rettermobile.rio.service.model

import java.io.Serializable

/**
 * Business error payload Rio returns from `TOKEN/signOut`.
 *
 * The endpoint can report a failure inside a 2xx response instead of a non-2xx
 * status, e.g. `{"message":"Your access token is invalid or expired",
 * "details":"jwt expired","code":"ACCESS_DENIED"}`. A non-blank [code] is what
 * marks the response as a failure.
 */
class RioSignOutErrorBody : Serializable {
    val code: String? = null
    val message: String? = null
    val details: String? = null
}
