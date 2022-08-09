package com.rbs.android.example.network

/**
 * Created by semihozkoroglu on 19.12.2021.
 */
data class TestResponse(val Bimages: List<Images>? = null, val CampaignDescription: String? = null, val AmpaignName: String?)
data class Images(val id: String, val url: String? = null)