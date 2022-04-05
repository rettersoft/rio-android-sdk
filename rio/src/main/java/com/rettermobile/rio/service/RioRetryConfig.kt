package com.rettermobile.rio.service

/**
 * Created by semihozkoroglu on 20.01.2022.
 */
class RioRetryConfig(
    var delay: Long = 50, // milisecond
    var count: Int = 3,
    var rate: Double = 1.5
)