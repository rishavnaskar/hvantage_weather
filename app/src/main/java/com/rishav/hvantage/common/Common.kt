package com.rishav.hvantage.common

import com.rishav.hvantage.`interface`.RetroFitService
import com.rishav.hvantage.retrofit.RetroFitClient

object Common {
    private const val baseUrl = "https://api.openweathermap.org/data/"
    val retroFitService: RetroFitService
        get() = RetroFitClient.getClient(baseUrl).create(RetroFitService::class.java)
}