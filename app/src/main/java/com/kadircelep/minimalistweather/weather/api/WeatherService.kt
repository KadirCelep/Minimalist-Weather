package com.kadircelep.minimalistweather.weather.api

import com.kadircelep.minimalistweather.weather.WeatherContract
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.Response

class WeatherService(private val okHttpClient: OkHttpClient) :
    WeatherContract.Service {

    //TODO: Move the credential out.
    private val baseUrl = "https://api.darksky.net/forecast/2bb07c3bece89caf533ac9a5d23d8417"

    override fun fetchForecast(
        latitude: Double,
        longitude: Double
    ): Response? {
        return okHttpClient
            .newCall(
                forecastRequest(
                    latitude,
                    longitude
                )
            )
            .execute()
    }

    private fun forecastRequest(
        latitude: Double,
        longitude: Double
    ): Request = Request.Builder()

        .url("$baseUrl/$latitude,$longitude")
        .build()
}