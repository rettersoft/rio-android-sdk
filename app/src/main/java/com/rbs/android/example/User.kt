package com.rbs.android.example

import com.rbs.android.UserUpdateEmailResponse
import com.rettermobile.rio.Rio
import com.rettermobile.rio.cloud.RioCallMethodOptions
import com.rettermobile.rio.cloud.RioCloudObject
import com.rettermobile.rio.cloud.RioGetCloudObjectOptions

class User private constructor(obj: RioCloudObject) {
    var _obj: RioCloudObject = obj

    companion object {
        fun getInstance(
            rio: Rio, options: RioGetCloudObjectOptions? = null, onSuccess: ((User) -> Unit)? = null,
            onError: ((Throwable?) -> Unit)? = null
        ) {
            val newOptions = options ?: RioGetCloudObjectOptions(
                classId = "MsisdnAuthenticator",
                body = mapOf(Pair("msisdn", "5333996030"))
            )

            rio.getCloudObject(newOptions, onSuccess = {
                onSuccess?.invoke(User(it))
            }, onError = {
                onError?.invoke(it)
            })
        }
    }

    fun updateEmail(
        input: UserUpdateEmailRequest? = null, options: RioCallMethodOptions? = null,
        onSuccess: ((UserUpdateEmailResponse?) -> Unit)? = null,
        onError: ((Throwable?) -> Unit)? = null
    ) {
        val newOptions = options ?: RioCallMethodOptions(
            method = "sendOtp12312",
            body = input
        )

        _obj.call<UserUpdateEmailResponse>(newOptions, onSuccess = {
            onSuccess?.invoke(it.body)
        }, onError = {
            onError?.invoke(it)
        })
    }
}