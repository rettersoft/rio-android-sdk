package com.rbs.android.example.network

/**
 * Created by semihozkoroglu on 19.12.2021.
 */
data class TestResponse(val data: TestDataResponse? = null)
data class TestDataResponse(val customToken: String?)

