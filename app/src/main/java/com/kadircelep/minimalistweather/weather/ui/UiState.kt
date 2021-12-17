package com.kadircelep.minimalistweather.weather.ui

sealed class UiState {
    object Loading : UiState()
    object RequestLocationPermission : UiState()
    object RefreshLocation : UiState()
    data class Content(val uiModel: WeatherUiModel) : UiState()
    data class Error(val errorUiModel: ErrorUiModel) : UiState()
}