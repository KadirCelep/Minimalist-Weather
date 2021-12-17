package com.kadircelep.minimalistweather.weather.domain

import java.util.*

data class Forecast(
    val time: Date,
    val summary: String,
    val temperature: Double
)
