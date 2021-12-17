package com.kadircelep.minimalistweather.weather.ui

data class ErrorUiModel(
    val message: Int,
    val actionMessage: Int,
    val action: () -> Unit
)
