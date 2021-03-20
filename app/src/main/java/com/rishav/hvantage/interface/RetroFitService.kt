package com.rishav.hvantage.`interface`

import com.rishav.hvantage.model.WeatherModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RetroFitService {
    @GET("2.5/weather")
    fun getData(
        @Query("lat") lat: String,
        @Query("lon") long: String,
        @Query("units") units: String,
        @Query("appid") appId: String
    ): Call<WeatherModel>
}