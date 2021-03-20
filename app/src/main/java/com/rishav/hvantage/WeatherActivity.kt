package com.rishav.hvantage

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.rishav.hvantage.`interface`.RetroFitService
import com.rishav.hvantage.common.Common
import com.rishav.hvantage.model.WeatherModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class WeatherActivity : AppCompatActivity() {
    lateinit var mService: RetroFitService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        val extras: Bundle? = intent.extras
        val lat: Double = extras!!.getDouble("lat")
        val long: Double = extras.getDouble("long")
        //val apiKey = System.getenv("apikey")!!.toString()

        mService = Common.retroFitService

        val progressBar: ProgressBar = findViewById(R.id.loader)
        val backButton: ImageButton = findViewById(R.id.weatherBackButton)
        val updatedAt: TextView = findViewById(R.id.updated_at)
        val status: TextView = findViewById(R.id.status)
        val temp: TextView = findViewById(R.id.temp)
        val minTemp: TextView = findViewById(R.id.temp_min)
        val maxTemp: TextView = findViewById(R.id.temp_max)
        val sunrise: TextView = findViewById(R.id.sunrise)
        val sunset: TextView = findViewById(R.id.sunset)
        val wind: TextView = findViewById(R.id.wind)
        val pressure: TextView = findViewById(R.id.pressure)
        val humidity: TextView = findViewById(R.id.humidity)

        backButton.setOnClickListener { finish() }

        getData(
            lat,
            long,
            "88de2a373b86a7f9b5b240ec97bd6a59",
            progressBar,
            updatedAt,
            status,
            temp,
            minTemp,
            maxTemp,
            sunrise,
            sunset,
            wind,
            pressure,
            humidity
        )
    }

    private fun getData(
        lat: Double,
        long: Double,
        apiKey: String,
        progressBar: ProgressBar,
        updatedAt: TextView,
        status: TextView,
        temp: TextView,
        minTemp: TextView,
        maxTemp: TextView,
        sunrise: TextView,
        sunset: TextView,
        wind: TextView,
        pressure: TextView,
        humidity: TextView
    ) {
        progressBar.visibility = View.VISIBLE
        mService.getData(lat.toString(), long.toString(), "metric", apiKey)
            .enqueue(object : Callback<WeatherModel> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<WeatherModel>,
                    response: Response<WeatherModel>
                ) {
                    progressBar.visibility = View.GONE

                    updatedAt.text = "Updated at ${dateConverter("MMM dd, yyyy HH:mm:ss a", response.body()!!.dt.toLong())}"
                    status.text = response.body()!!.weather[0].main
                    temp.text = response.body()!!.main.temp.toString()
                    minTemp.text = "Min: ${response.body()!!.main.temp_min}"
                    maxTemp.text = "Max: ${response.body()!!.main.temp_max}"
                    sunrise.text = dateConverter("HH:mm:ss a", response.body()!!.sys.sunrise.toLong())
                    sunset.text = dateConverter("HH:mm:ss a", response.body()!!.sys.sunrise.toLong())
                    wind.text = "${response.body()!!.wind.speed} m/s"
                    pressure.text = "${response.body()!!.main.pressure} atm"
                    humidity.text = "${response.body()!!.main.humidity} %"
                }

                override fun onFailure(call: Call<WeatherModel>, t: Throwable) {
                    println("ERROR - " + t.message)
                }

            })
    }

    private fun dateConverter(pattern: String, unix_seconds: Long): String {
        val date = Date(unix_seconds * 1000L)
        val jdf = SimpleDateFormat(pattern, Locale.ENGLISH)
        jdf.timeZone = TimeZone.getTimeZone("GMT-4")

        return jdf.format(date)
    }
}