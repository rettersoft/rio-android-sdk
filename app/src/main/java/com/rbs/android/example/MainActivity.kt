package com.rbs.android.example

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.gson.Gson
import com.rbs.android.example.network.TestRequest
import com.rbs.android.example.network.TestResponse
import com.rettermobile.rio.Rio
import com.rettermobile.rio.RioLogger
import com.rettermobile.rio.cloud.RioCallMethodOptions
import com.rettermobile.rio.cloud.RioCloudObjectOptions
import com.rettermobile.rio.cloud.RioErrorResponse
import com.rettermobile.rio.model.RioClientAuthStatus
import com.rettermobile.rio.util.RioHttpMethod

class MainActivity : AppCompatActivity() {

    lateinit var btnGetCloudCall: Button
    lateinit var btnSignIn: Button
    lateinit var btnSignOut: Button
    lateinit var loading: ProgressBar

    private lateinit var rio: Rio

    private val errorListener: (Throwable?) -> Unit = {
        if (it is RioErrorResponse) {
            it.body<BaseResponse>()?.code
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rio = (application as App).rio

        rio.setOnClientAuthStatusChangeListener { rbsClientAuthStatus, rbsUser ->
            RioLogger.log(
                "RIOClientAuthStatus: $rbsClientAuthStatus rbsUser: ${
                    Gson().toJson(
                        rbsUser
                    )
                }"
            )

            rio.getCloudObject(RioCloudObjectOptions(classId = "User", instanceId = "231321", useLocal = true), onSuccess = { cloudObj ->

                cloudObj.call<TestResponse>(RioCallMethodOptions("getProfile", httpMethod = RioHttpMethod.GET, queries = mapOf(Pair("key1", "value1"), Pair("key2", 2312))), onSuccess = {
                    Log.e("", "")
                }, onError = {
                    Log.e("", "")

                    if (it is RioErrorResponse) {
                        it.rawBody
                    }
                })

                loading.isVisible = true
                cloudObj.user.subscribe(eventFired = {
                    loading.isVisible = false
                }, errorFired = {
                    loading.isVisible = false
                })
            }, onError = { throwable ->
                Log.e("", "")
            })
        }

//        rio.signOut { isSuc, throwable -> }

        btnGetCloudCall = findViewById(R.id.btnGetCloudCall)
        btnSignIn = findViewById(R.id.btnSignIn)
        btnSignOut = findViewById(R.id.btnSignOut)
        loading = findViewById(R.id.loading)
        loading.isVisible = false

        // 'https://6062mhn7s.test-api.retter.io/6062mhn7s/CALL/token/sayHello/01gfvgbnajkfwnn81ex2ft5sjy '{"userId":"ali","identity":"enduser"}'
//        https://q3glt327r.api.retter.io/q3glt327r/CALL/StaticMethodTest/sayHello/param1/param2/param3/param4
        btnGetCloudCall.setOnClickListener {
            rio.makeStaticCall<TestResponse>(
                options = RioCloudObjectOptions(
                    classId = "TcknAuthenticator",
                    method = "mobileAuth",
                    body = TestRequest()
                ), onSuccess = {
                    if (rio.getAuthStatus() != RioClientAuthStatus.SIGNED_IN) {
                        rio.authenticateWithCustomToken(it.body?.customToken ?: "")
                    }
                }, onError = {

                })
        }

        btnSignOut.setOnClickListener { rio.signOut() }
    }
}