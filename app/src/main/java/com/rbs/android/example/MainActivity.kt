package com.rbs.android.example

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.ContentLoadingProgressBar
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
import retrofit2.HttpException


class MainActivity : AppCompatActivity() {

    lateinit var rvLogs: RecyclerView
    lateinit var btnClearLog: Button
    lateinit var signInAnonymously: Button
    lateinit var btnGetCloudCreate: Button
    lateinit var btnGetCloudCall: Button
    lateinit var btnSignIn: Button
    lateinit var btnGenerate: Button
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
        btnGenerate = findViewById(R.id.btnGenerate)
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
                    options = RBSCallMethodOptions(method = "getToken"),
                    onSuccess = {
//                    rbs.authenticateWithCustomToken(auth.customToken)
//                    RBSLogger.log("AUTHENTICATE YES")
                    })
            }
        }

        btnGenerate.setOnClickListener {
            rbs.generateGetActionUrl(
                action = "rbs.address.get.COUNTRIES",
                data = mapOf(Pair("cartId", "1de255c877")),
                success = { jsonData ->
                    Log.e("RBSService", jsonData!!) // Convert to data model with Gson()
                },
                error = {
                    if (it is HttpException) {
                        if (it.code() == 302) {
                            // redirect to login
                        }
                    }
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Status")
                    builder.setMessage(it?.message)
                    builder.setPositiveButton(android.R.string.yes) { dialog, which -> }
                    builder.show()
                })

            val jsonData = rbs.generatePublicGetActionUrl(
                action = "rbs.address.get.COUNTRIES",
                data = mapOf(Pair("cartId", "1de255c877"))
            )

            Log.e("RBSService", jsonData) // Convert to data model with Gson()
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
                        val headers = it.headers()
                        val code = it.code()
                        val body = it.body()

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
                classId = "TestClass",
                instanceId = "01FQXSX0S23GQA59ZS45H66YGC"
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