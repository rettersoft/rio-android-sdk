package com.rbs.android.example

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rbs.android.example.network.TestNetwork
import com.rettermobile.rbs.RBS
import com.rettermobile.rbs.util.RBSRegion
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rbs = RBS(
            applicationContext = applicationContext,
            projectId = "3b7eea955170401685ec7ac0187ef787",
            region = RBSRegion.EU_WEST_1_BETA
        )

        rbs.setOnClientAuthStatusChangeListener { rbsClientAuthStatus, rbsUser ->
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Status")
            builder.setMessage(rbsClientAuthStatus.name + " " + rbsUser?.uid)
            builder.setPositiveButton(android.R.string.yes) { dialog, which -> }
            builder.show()
        }

        btnSignIn.setOnClickListener {
            rbs.sendAction(
                action = "rbs.businessuserauth.request.LOGIN",
                data = mapOf(Pair("email", "root"), Pair("password", "12345")),
                headers = mapOf(Pair("header1", "parameter 1"), Pair("header2", "parameter 2"), Pair("header3", "parameter 3")),
                success = {

                    val type = object : TypeToken<List<AuthResponse>>() {}.type
                    val items: List<AuthResponse> = Gson().fromJson(it!!, type)

                    rbs.authenticateWithCustomToken(items[0].response!!.customToken)
                }, error = {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Status")
                    builder.setMessage(it?.message)
                    builder.setPositiveButton(android.R.string.yes) { dialog, which -> }
                    builder.show()
                }
            )
        }

        btnSearch.setOnClickListener {
            rbs.sendAction(
                action = "rbs.address.get.COUNTRIES",
                data = mapOf(Pair("cartId", "1de255c877")),
                headers = mapOf(Pair("header1", "parameter 1"), Pair("header2", "parameter 2"), Pair("header3", "parameter 3")),
                success = { jsonData ->
                    Log.e("RBSService", jsonData) // Convert to data model with Gson()

                    Toast.makeText(this, jsonData, Toast.LENGTH_LONG).show()
                },
                error = {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Status")
                    builder.setMessage(it?.message)
                    builder.setPositiveButton(android.R.string.yes) { dialog, which -> }
                    builder.show()
                })

            // POST action
//            rbs.sendAction(action = "rbs.cart.request.GET", data = mapOf(Pair("cartId", "1de255c877")), success = { jsonData ->
//                Log.e("RBSService", jsonData) // Convert to data model with Gson()
//
//                Toast.makeText(this, jsonData, Toast.LENGTH_LONG).show()
//            }, error = {
//                val builder = AlertDialog.Builder(this)
//                builder.setTitle("Status")
//                builder.setMessage(it?.message)
//                builder.setPositiveButton(android.R.string.yes) { dialog, which -> }
//                builder.show()
//            })
        }

        btnGenerate.setOnClickListener {
            rbs.generateGetActionUrl(
                action = "rbs.address.get.COUNTRIES",
                data = mapOf(Pair("cartId", "1de255c877")),
                success = { jsonData ->
                    Log.e("RBSService", jsonData) // Convert to data model with Gson()

                    Toast.makeText(this, jsonData, Toast.LENGTH_LONG).show()

                    GlobalScope.launch {
                        TestNetwork().getConnection(RBSRegion.EU_WEST_1_BETA.getUrl).get(jsonData!!)
                    }
                },
                error = {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Status")
                    builder.setMessage(it?.message)
                    builder.setPositiveButton(android.R.string.yes) { dialog, which -> }
                    builder.show()
                })
        }

        btnSignOut.setOnClickListener { rbs.signOut() }
    }
}