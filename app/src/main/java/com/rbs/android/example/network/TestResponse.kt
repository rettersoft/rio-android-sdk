package com.rbs.android.example.network

/**
 * Created by semihozkoroglu on 19.12.2021.
 */
data class TestResponse(val CampaignName: String?, val CampaignDescription: String? = null, val images: List<Images>? = null)
data class Images(val id: String, val url: String? = null)