package com.rbs.android.example

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rettermobile.rbs.RBS
import com.rettermobile.rbs.RBSLogger
import com.rettermobile.rbs.cloud.RBSCloudObject
import com.rettermobile.rbs.cloud.RBSCloudObjectOptions
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
                options = RBSCloudObjectOptions(method = "getToken"),
                eventFired = {
                    val auth = Gson().fromJson(it, AuthModel::class.java)

                    rbs.authenticateWithCustomToken(auth.customToken)
                    RBSLogger.log("AUTHENTICATE YES")
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

        btnSignOut.setOnClickListener { rbs.signOut() }
    }

    private fun createCloudObject() {
        rbs.getCloudObject(
            options = RBSCloudObjectOptions(
                classId = "AndroidTest",
                instanceId = "01FPTD2HAQGB5BS7J01S0QD7Q2"
            ),
            success = { cloudObj ->
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


    private inline fun <reified T> getResponse(json: String): T? {
        val gson = Gson()
        val type = object : TypeToken<List<Map<String, Any>>>() {}.type

        val response = gson.fromJson<List<Map<String, Any>>>(json, type)

        return if (response.isNullOrEmpty()) null else gson.fromJson<T>(
            gson.toJson(response[0]["response"]),
            T::class.java
        )
    }
}