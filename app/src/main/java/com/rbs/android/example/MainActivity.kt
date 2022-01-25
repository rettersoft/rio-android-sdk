package com.rbs.android.example

import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.rbs.android.example.network.TestRequest
import com.rbs.android.example.network.TestResponse
import com.rettermobile.rbs.RBS
import com.rettermobile.rbs.RBSLogger
import com.rettermobile.rbs.cloud.RBSCallMethodOptions
import com.rettermobile.rbs.cloud.RBSCloudObject
import com.rettermobile.rbs.cloud.RBSGetCloudObjectOptions
import com.rettermobile.rbs.util.Logger


class MainActivity : AppCompatActivity() {

    lateinit var rvLogs: RecyclerView
    lateinit var btnClearLog: Button
    lateinit var signInAnonymously: Button
    lateinit var btnGetCloudCreate: Button
    lateinit var btnGetCloudCall: Button
    lateinit var btnSignIn: Button
    lateinit var btnSignOut: Button
    lateinit var loading: ProgressBar

    private val items = ArrayList<String>()

    private lateinit var rbs: RBS
    private var cloudObj: RBSCloudObject? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rbs = (application as App).rbs

        rbs.setOnClientAuthStatusChangeListener { rbsClientAuthStatus, rbsUser ->
            RBSLogger.log(
                "rbsClientAuthStatus: $rbsClientAuthStatus rbsUser: ${
                    Gson().toJson(
                        rbsUser
                    )
                }"
            )
        }

        rvLogs = findViewById(R.id.rvLogs)
        btnGetCloudCall = findViewById(R.id.btnGetCloudCall)
        btnGetCloudCreate = findViewById(R.id.btnGetCloudCreate)
        btnClearLog = findViewById(R.id.btnClearLog)
        signInAnonymously = findViewById(R.id.signInAnonymously)
        btnSignIn = findViewById(R.id.btnSignIn)
        btnSignOut = findViewById(R.id.btnSignOut)
        loading = findViewById(R.id.loading)
        loading.isVisible = false

        rvLogs.adapter = LogAdapter(items)

        signInAnonymously.setOnClickListener { rbs.signInAnonymously() }

        btnClearLog.setOnClickListener {
            runOnUiThread {
                items.clear()
                rvLogs.adapter?.notifyDataSetChanged()
            }
        }

        btnGetCloudCreate.setOnClickListener { createCloudObject() }

        rbs.setLoggerListener(object : Logger {
            override fun log(message: String) {
                runOnUiThread {
                    items.add(message)
                    rvLogs.adapter?.notifyDataSetChanged()
                }
            }
        })

        btnSignIn.setOnClickListener {
            if (cloudObj == null) {
                createCloudObject()
            } else {
                cloudObj?.call<TestResponse>(
                    options = RBSCallMethodOptions(method = "test"),
                    onSuccess = {
                        RBSLogger.log("CustomToken: ${it.body?.customToken}")
                        rbs.authenticateWithCustomToken(it.body?.customToken!!)
                        RBSLogger.log("AUTHENTICATE YES")
                    })
            }
        }

        btnGetCloudCall.setOnClickListener {
            if (cloudObj == null) {
                createCloudObject()
            } else {
                cloudObj?.call<TestResponse>(
                    options = RBSCallMethodOptions(
                        method = "sayHello",
                        body = TestRequest()
                    ),
                    onSuccess = {
                        val headers = it.headers
                        val code = it.code
                        val body = it.body

                        RBSLogger.log("HEADERS ${Gson().toJson(headers)}")
                        RBSLogger.log("CODE ${Gson().toJson(code)}")
                        RBSLogger.log("BODY ${Gson().toJson(body)}")
                    }, onError = {
                        // ConnectionTimeOut
                    })
            }
        }

        btnSignOut.setOnClickListener { rbs.signOut() }
    }

    private fun createCloudObject() {
        loading.isVisible = true

        rbs.getCloudObject(
            options = RBSGetCloudObjectOptions(
                classId = "Semih"
            ),
            onSuccess = { cloudObj ->
                this@MainActivity.cloudObj = cloudObj

                cloudObj.user.subscribe(eventFired = {
                    RBSLogger.log("SUCCESS USER $it")
                }, errorFired = {
                    RBSLogger.log("SUCCESS USER ${it?.message}")
                })

                cloudObj.role.subscribe(eventFired = {
                    RBSLogger.log("SUCCESS ROLE $it")
                }, errorFired = {
                    RBSLogger.log("SUCCESS ROLE ${it?.message}")
                })

                cloudObj.public.subscribe(eventFired = {
                    RBSLogger.log("SUCCESS PUBLIC $it")
                }, errorFired = {
                    RBSLogger.log("SUCCESS PUBLIC ${it?.message}")
                })

                loading.isVisible = false
            }, onError = {
                loading.isVisible = false
            })
    }
}