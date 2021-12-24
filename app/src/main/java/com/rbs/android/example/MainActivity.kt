package com.rbs.android.example

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.rbs.android.example.network.TestRequest
import com.rbs.android.example.network.TestResponse
import com.rettermobile.rbs.RBS
import com.rettermobile.rbs.RBSLogger
import com.rettermobile.rbs.cloud.RBSCallMethodOptions
import com.rettermobile.rbs.cloud.RBSCloudObject
import com.rettermobile.rbs.cloud.RBSGetCloudObjectOptions
import com.rettermobile.rbs.model.RBSClientAuthStatus
import com.rettermobile.rbs.util.Logger


class MainActivity : AppCompatActivity() {

    lateinit var rvLogs: RecyclerView
    lateinit var btnClearLog: Button
    lateinit var btnGetCloud: Button
    lateinit var btnGetCloudCall: Button
    lateinit var btnSignIn: Button
    lateinit var btnGenerate: Button
    lateinit var btnSignOut: Button

    private val items = ArrayList<String>()

    private lateinit var rbs: RBS
    private var cloudObj: RBSCloudObject? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rbs = (application as App).rbs

        rvLogs = findViewById(R.id.rvLogs)
        btnGetCloud = findViewById(R.id.btnGetCloud)
        btnGetCloudCall = findViewById(R.id.btnGetCloudCall)
        btnClearLog = findViewById(R.id.btnClearLog)
        btnSignIn = findViewById(R.id.btnSignIn)
        btnGenerate = findViewById(R.id.btnGenerate)
        btnSignOut = findViewById(R.id.btnSignOut)

        rvLogs.adapter = LogAdapter(items)

        createCloudObject()

        btnClearLog.setOnClickListener {
            runOnUiThread {
                items.clear()
                rvLogs.adapter?.notifyDataSetChanged()
            }
        }

        rbs.setLoggerListener(object : Logger {
            override fun log(message: String) {
                runOnUiThread {
                    items.add(message)
                    rvLogs.adapter?.notifyDataSetChanged()
                }
            }
        })

        (application as App).rbs.setOnClientAuthStatusChangeListener { rbsClientAuthStatus, rbsUser ->
            if (rbsClientAuthStatus == RBSClientAuthStatus.SIGNED_IN) {
                createCloudObject()
            }
        }

        btnSignIn.setOnClickListener {
            cloudObj?.call(
                options = RBSCallMethodOptions(method = "getToken"),
                onSuccess = {
//                    val auth = Gson().fromJson(it, AuthModel::class.java)
//
//                    rbs.authenticateWithCustomToken(auth.customToken)
//                    RBSLogger.log("AUTHENTICATE YES")
                })
        }

        btnGenerate.setOnClickListener {
            rbs.generateGetActionUrl(
                action = "rbs.address.get.COUNTRIES",
                data = mapOf(Pair("cartId", "1de255c877")),
                success = { jsonData ->
                    Log.e("RBSService", jsonData!!) // Convert to data model with Gson()
                },
                error = {
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

        btnGetCloud.setOnClickListener {
            createCloudObject()
        }

        btnGetCloud.setOnClickListener {
            cloudObj?.call(
                options = RBSCallMethodOptions(
                    method = "sayHello",
                    body = TestRequest()
                ),
                onSuccess = {
                    val headers = it?.headers()
                    val code = it?.code()
                    val body = it?.body<TestResponse>()

                    RBSLogger.log("HEADERS ${Gson().toJson(headers)}")
                    RBSLogger.log("CODE ${Gson().toJson(code)}")
                    RBSLogger.log("BODY ${Gson().toJson(body)}")
                }, onError = {
                    // ConnectionTimeOut
                })
        }

        btnSignOut.setOnClickListener { rbs.signOut() }
    }

    private fun createCloudObject() {
        rbs.getCloudObject(
            options = RBSGetCloudObjectOptions(
                classId = "ChatRoom",
//                instanceId = "01FQ4BE0S74DNSPRERE0H6HQDN"
            ),
            onSuccess = { cloudObj ->
                this@MainActivity.cloudObj = cloudObj

                cloudObj?.user?.subscribe(eventFired = {
                    RBSLogger.log("SUCCESS USER $it")
                }, errorFired = {
                    RBSLogger.log("SUCCESS USER ${it?.message}")
                })

                cloudObj?.role?.subscribe(eventFired = {
                    RBSLogger.log("SUCCESS ROLE $it")
                }, errorFired = {
                    RBSLogger.log("SUCCESS ROLE ${it?.message}")
                })

                cloudObj?.public?.subscribe(eventFired = {
                    RBSLogger.log("SUCCESS PUBLIC $it")
                }, errorFired = {
                    RBSLogger.log("SUCCESS PUBLIC ${it?.message}")
                })
            })
    }
}