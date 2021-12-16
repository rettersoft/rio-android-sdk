package com.rbs.android.example

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rettermobile.rbs.RBS
import com.rettermobile.rbs.model.RBSCulture
import com.rettermobile.rbs.util.Logger
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener


class MainActivity : AppCompatActivity() {

    lateinit var rvLogs: RecyclerView
    lateinit var tvState: AppCompatTextView
    lateinit var btnClearLog: Button
    lateinit var btnSignIn: Button
    lateinit var btnSocket: Button
    lateinit var btnGenerate: Button
    lateinit var btnSignOut: Button

    private val items = ArrayList<String>()

    private lateinit var rbs: RBS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rbs = (application as App).rbs

        rvLogs = findViewById(R.id.rvLogs)
        tvState = findViewById(R.id.tvState)
        btnClearLog = findViewById(R.id.btnClearLog)
        btnSignIn = findViewById(R.id.btnSignIn)
        btnSocket = findViewById(R.id.btnSocket)
        btnGenerate = findViewById(R.id.btnGenerate)
        btnSignOut = findViewById(R.id.btnSignOut)

        rvLogs.adapter = LogAdapter(items)
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
//            val builder = AlertDialog.Builder(this)
//            builder.setTitle("Status")
//            builder.setMessage(rbsClientAuthStatus.name + " " + rbsUser?.uid)
//            builder.setPositiveButton(android.R.string.yes) { dialog, which -> }
//            builder.show()
        }

        btnSignIn.setOnClickListener {
            rbs.authenticateWithCustomToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZGVudGl0eSI6ImVuZHVzZXIiLCJhbm9ueW1vdXMiOmZhbHNlLCJwcm9qZWN0SWQiOiIwNDhkYmY0YWI4Nzg0ODc4OTUxMjlhMGM3NzhlNzk5NiIsInVzZXJJZCI6IlRFU1QiLCJ0aW1lc3RhbXAiOjE2MzY3MDg0ODkzNjIsImNsYWltcyI6e30sImlhdCI6MTYzNjcwODQ4OSwiZXhwIjoxNjQ2NzA4NDg5LCJpc3MiOiJjb3JlLXRlc3QucmV0dGVybW9iaWxlLmNvbSJ9.poi6dmvj1l4CITfYno8YKICGvpQWvh4crsl8iZoIDmc")

//            rbs.sendAction(
//                action = "rbs.core.request.GENERATE_CUSTOM_TOKEN",
//                data = mapOf(Pair("email", "TEST"), Pair("password", "12345678")),
//                culture = RBSCulture.TR,
//                success = { jsonData ->
//                    Log.e("RBSService", jsonData) // Convert to data model with Gson()
//
////                    Toast.makeText(this, jsonData, Toast.LENGTH_LONG).show()
//                },
//                error = {
//                    val builder = AlertDialog.Builder(this)
//                    builder.setTitle("Status")
//                    builder.setMessage(it?.message)
//                    builder.setPositiveButton(android.R.string.yes) { dialog, which -> }
//                    builder.show()
//                })

//            rbs.sendAction(
//                action = "rbs.businessuserauth.request.LOGIN",
//                data = mapOf(Pair("email", "ahmet"), Pair("password", "12345678")),
//                culture = RBSCulture.TR,
//                success = { jsonData ->
//                    Log.e("RBSService", jsonData) // Convert to data model with Gson()
//
////                    Toast.makeText(this, jsonData, Toast.LENGTH_LONG).show()
//                },
//                error = {
//                    val builder = AlertDialog.Builder(this)
//                    builder.setTitle("Status")
//                    builder.setMessage(it?.message)
//                    builder.setPositiveButton(android.R.string.yes) { dialog, which -> }
//                    builder.show()
//                })
        }

        btnSocket.setOnClickListener {

            rbs.sendAction(
                action = "rbs.process.request.START",
                data = mapOf(Pair("processId", "MERT_TEST")),
                culture = RBSCulture.TR,
                success = { jsonData ->
                    Log.e("RBSService", jsonData) // Convert to data model with Gson()

//                    Toast.makeText(this, jsonData, Toast.LENGTH_LONG).show()
                },
                error = {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Status")
                    builder.setMessage(it?.message)
                    builder.setPositiveButton(android.R.string.yes) { dialog, which -> }
                    builder.show()
                })
        }

        btnGenerate.setOnClickListener {
            rbs.generateGetActionUrl(
                action = "rbs.address.get.COUNTRIES",
                data = mapOf(Pair("cartId", "1de255c877")),
                success = { jsonData ->
                    Log.e("RBSService", jsonData) // Convert to data model with Gson()

//                    Toast.makeText(this, jsonData, Toast.LENGTH_LONG).show()

//                    GlobalScope.launch {
//                        TestNetwork().getConnection(RBSRegion.EU_WEST_1_BETA.getUrl).get(jsonData!!)
//                    }
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

//            Toast.makeText(this, jsonData, Toast.LENGTH_LONG).show()
        }

        btnSignOut.setOnClickListener { rbs.signOut() }
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