package com.rbs.android.example

import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.gson.Gson
import com.rettermobile.rio.Rio
import com.rettermobile.rio.RioLogger

class MainActivity : AppCompatActivity() {

    lateinit var signInAnonymously: Button
    lateinit var btnGetCloudCall: Button
    lateinit var btnSignIn: Button
    lateinit var btnSignOut: Button
    lateinit var loading: ProgressBar

    private lateinit var rio: Rio

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

        signInAnonymously.setOnClickListener { rio.signInAnonymously() }

        btnGetCloudCall.setOnClickListener {
            User.getInstance(rio, onSuccess = {
                it.updateEmail(UserUpdateEmailRequest(), onSuccess = {

                }, onError = {

                })
            })
        }

        btnSignOut.setOnClickListener { rio.signOut() }
    }
}