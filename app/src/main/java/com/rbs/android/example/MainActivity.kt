package com.rbs.android.example

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.gson.Gson
import com.rettermobile.rio.Rio
import com.rettermobile.rio.RioLogger
import com.rettermobile.rio.cloud.RioCloudObjectOptions
import com.rettermobile.rio.cloud.RioErrorResponse

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
            rio.getCloudObject(RioCloudObjectOptions(classId = "Semih"), onSuccess = {
                it.listInstances(onSuccess = {
                    RioLogger.log("RESPONSE CAME: ${Gson().toJson(it)}")
                }, onError = {
                    RioLogger.log("ERROR CAME: ${it?.message}")
                })
            }, onError = {
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