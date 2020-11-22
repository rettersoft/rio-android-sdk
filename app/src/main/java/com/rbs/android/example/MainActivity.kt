package com.rbs.android.example

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
            projectId = "7b7ecec721d54629bed1d3b1aec210e8",
            clientId = "rbs.user.enduser"
        )

        rbs.authenticateWithCustomToken(testCustomToken)

        btnClick.setOnClickListener {
            rbs.sendAction(action = "rbs.oms.request.GET_MY_ORDERS", success = {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }, error = {
                Toast.makeText(this, it?.message, Toast.LENGTH_LONG).show()
            })
        }
    }
}