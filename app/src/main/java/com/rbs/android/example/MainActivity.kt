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
            rio.makeStaticCall<TestResponse>(options = RioCloudObjectOptions(classId = "StaticMethodTest", method = "sayHello", path = "param1/param2/param3/param4"), onSuccess = {

            }, onError = {

            })
            rio.getCloudObject(RioCloudObjectOptions(classId = "token", instanceId = "01gfvgbnajkfwnn81ex2ft5sjy"), onSuccess = { cloudObj ->

                cloudObj.call<TestResponse>(RioCallMethodOptions("sayHello", httpMethod = RioHttpMethod.POST, body = TestRequest()), onSuccess = {
                    Log.e("", "")
                    rio.authenticateWithCustomToken(it.body?.data?.customToken ?: "")
                }, onError = {
                    Log.e("", "")
                })

                loading.isVisible = true
                cloudObj.user.subscribe(eventFired = {
                    loading.isVisible = false
                }, errorFired = {
                    loading.isVisible = false
                })
            }, onError = { throwable ->
            })
        }

        btnSignOut.setOnClickListener { rio.signOut() }
    }
}