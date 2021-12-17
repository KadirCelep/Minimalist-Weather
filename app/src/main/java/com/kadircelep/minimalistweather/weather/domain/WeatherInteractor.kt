package com.kadircelep.minimalistweather.weather.domain

import androidx.annotation.VisibleForTesting
import com.kadircelep.minimalistweather.weather.WeatherContract
import org.json.JSONObject
import java.sql.Date


class WeatherInteractor(
    private val weatherService: WeatherContract.Service
) : WeatherContract.Interactor {
    override fun getForecast(latitude: Double, longitude: Double): Forecast? {
        return weatherService.fetchForecast(latitude, longitude)?.let { response ->
            response.body()?.string()?.let { body ->
                parseForecast(body)
            }
        }

    }

    @VisibleForTesting
    fun parseForecast(body: String): Forecast? {
        val jsonObject = JSONObject(body)
        return jsonObject.getJSONObject("currently")?.let { current ->
            Forecast(
                Date(current.getLong("time")),
                current.getString("summary"),
                current.getDouble("temperature")
            )

        }
    }
}
