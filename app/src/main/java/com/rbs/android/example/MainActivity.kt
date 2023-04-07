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

    lateinit var btnGetCloud: Button
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

        btnGetCloud = findViewById(R.id.btnGetCloud)
        btnGetCloudCall = findViewById(R.id.btnGetCloudCall)
        btnSignIn = findViewById(R.id.btnSignIn)
        btnSignOut = findViewById(R.id.btnSignOut)
        loading = findViewById(R.id.loading)
        loading.isVisible = false

        btnGetCloud.setOnClickListener {
            rio.getCloudObject(
                RioCloudObjectOptions(
                    classId = "superFour",
                    instanceId = "01gx697h6k7v56tsadzatga2pn",
                    useLocal = true
                ), onSuccess = {
                    it.call<TestResponse>(RioCallMethodOptions(method = "generateToken"), onSuccess = {
                        rio.authenticateWithCustomToken(it.body?.customToken ?: "")
                    }, onError = {

                    })
                }, onError = {

                })
        }

        btnGetCloudCall.setOnClickListener {
            rio.makeStaticCall<TestResponse>(
                options = RioCloudObjectOptions(
                    classId = "ExampleProject",
                    method = "ExampleMethod",
                    body = TestRequest()
                ), onSuccess = {
                }, onError = {

                })
        }

        btnSignOut.setOnClickListener { rio.signOut() }
    }
}