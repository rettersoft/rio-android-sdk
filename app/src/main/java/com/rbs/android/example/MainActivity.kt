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
import com.rettermobile.rio.service.RioRetryConfig
import com.rettermobile.rio.util.RioHttpMethod
import okhttp3.internal.http.HttpMethod

class MainActivity : AppCompatActivity() {

    lateinit var signInAnonymously: Button
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
                "rbsClientAuthStatus: $rbsClientAuthStatus rbsUser: ${
                    Gson().toJson(
                        rbsUser
                    )
                }"
            )
        }

        rio.signOut { isSuc, throwable -> }

        btnGetCloudCall = findViewById(R.id.btnGetCloudCall)
        signInAnonymously = findViewById(R.id.signInAnonymously)
        btnSignIn = findViewById(R.id.btnSignIn)
        btnSignOut = findViewById(R.id.btnSignOut)
        loading = findViewById(R.id.loading)
        loading.isVisible = false

        signInAnonymously.setOnClickListener {
            rio.signInAnonymously(callback = { isSuccess, th ->
                Log.e("", "")
            })
        }

        btnGetCloudCall.setOnClickListener {
            rio.getCloudObject(RioCloudObjectOptions(classId = "QSyncTest"), onSuccess = { cloudObj ->

                val query = HashMap<String, Any>()

                query["title"] = "HELLO NABER"
                query["BOOL"] = true
                query["LONG"] = 100000
                query["typeList"] = listOf("SLOT", "XXX", "YYYY")
                query["IntList"] = listOf(1, 2, 3)

                val body = TestRequest().apply {
                    param1 = "Lorem ipsum"
                    param2 = "Lorem ipsum 2"
                }

                cloudObj.call<TestResponse>(RioCallMethodOptions(method = "sayHelloSync", httpMethod = RioHttpMethod.GET, queries = query, body = body), onSuccess = {
                    Log.e("", "")
                }, onError = {
                    Log.e("", "")
                })

//                Gson().toJson(it.response)
//
//                it.listInstances(onSuccess = {
//                    RioLogger.log("RESPONSE CAME: ${Gson().toJson(it)}")
//                }, onError = {
//                    RioLogger.log("ERROR CAME: ${it?.message}")
//                })
//
//                cloudObj.user.subscribe(eventFired = {
//
//                }, errorFired = { throwable ->
//
//                })
            }, onError = { throwable ->
            })
//            User.getInstance(rio, onSuccess = {
//                it.updateEmail(UserUpdateEmailRequest(), onSuccess = {
//                }, onError = errorListener)
//            }, onError = errorListener)
//            rio.authenticateWithCustomToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJwcm9qZWN0SWQiOiJza3VmNzZtMG8iLCJpZGVudGl0eSI6ImVuZHVzZXIiLCJhbm9ueW1vdXMiOmZhbHNlLCJ1c2VySWQiOiI5MDUzMDQ5MTQ1OTciLCJjbGFpbXMiOnsibXNpc2RuIjoiOTA1MzA0OTE0NTk3In0sImlhdCI6MTY0NTI2MjQxMCwiZXhwIjoxNjQ1MjYyNDQwfQ.o5LD193aiHLByvQTz7aozdYyqR9gc-a1vH8Bb_pPnaU")
        }

        btnSignOut.setOnClickListener { rio.signOut() }
    }
}