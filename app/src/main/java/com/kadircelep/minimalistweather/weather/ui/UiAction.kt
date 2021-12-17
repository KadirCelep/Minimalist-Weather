package com.kadircelep.minimalistweather.weather.ui

sealed class UiAction {
    data class LocationUpdated(
        val latitude: Double,
        val longitude: Double
    ) : UiAction()

    data class RetryClicked(
        val latitude: Double,
        val longitude: Double
    ) : UiAction()

    object LocationPermissionDenied : UiAction()
    object LocationPermissionGranted : UiAction()
}
