package com.rbs.android.example

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rettermobile.rbs.RBS
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val testCustomToken =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjbGllbnRJZCI6InJicy51c2VyLmVuZHVzZXIiLCJhbm9ueW1vdXMiOmZhbHNlLCJwcm9qZWN0SWQiOiI3YjdlY2VjNzIxZDU0NjI5YmVkMWQzYjFhZWMyMTBlOCIsInVzZXJJZCI6Im15VXNlcklkMSIsInRpbWVzdGFtcCI6MTYwNTgwOTkwMjAxMiwic2VydmljZUlkIjoidGVzdHNlcnZpY2UiLCJpYXQiOjE2MDU4MDk5MDIsImV4cCI6MTYwNzEwNTkwMn0.O1xaYQzdG7awq_jt5PxrezKTtR7OG4BEa0AxOvpTt60"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rbs = RBS(
            applicationContext = applicationContext,
            projectId = "933a51e1c87a9ccc181d21fca91c2aad"
        )

        rbs.setOnClientAuthStatusChangeListener { rbsClientAuthStatus, rbsUser ->
            runOnUiThread {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Status")
                builder.setMessage(rbsClientAuthStatus.name + " " + rbsUser?.uid)
                builder.setPositiveButton(android.R.string.yes) { dialog, which -> }
                builder.show()
            }
        }

        btnSignIn.setOnClickListener {
            rbs.sendAction(
                action = "rbs.businessuserauth.request.LOGIN",
                data = mapOf(Pair("email", "email@test.com"), Pair("password", "password")),
                success = {

                    val type = object : TypeToken<List<AuthResponse>>() {}.type
                    val items: List<AuthResponse> = Gson().fromJson(it!!, type)

                    rbs.authenticateWithCustomToken(items[0].response!!.customToken)
                }
            )
        }

        btnSearch.setOnClickListener {
            rbs.sendAction(action = "rbs.product.request.SEARCH", success = { jsonData ->
                Log.e("RBSService", jsonData) // Convert to data model with Gson()

                Toast.makeText(this, jsonData, Toast.LENGTH_LONG).show()
            }, error = {
                Toast.makeText(this, it?.message, Toast.LENGTH_LONG).show()
            })
        }

        btnSignOut.setOnClickListener { rbs.signOut() }
    }
}