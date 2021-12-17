package com.kadircelep.minimalistweather.weather

import com.kadircelep.minimalistweather.weather.domain.Forecast
import com.kadircelep.minimalistweather.weather.ui.UiAction
import com.kadircelep.minimalistweather.weather.ui.UiState
import com.squareup.okhttp.Response

interface WeatherContract {
    interface View {
        fun onState(uiState: UiState)
    }

    interface Presenter {
        fun attach(viewToAttach: View)
        fun onUiAction(uiAction: UiAction)
        fun detach()
    }

    interface Interactor{
        fun getForecast(latitude: Double, longitude: Double): Forecast?
    }

    interface Service {
        fun fetchForecast(latitude: Double, longitude: Double): Response?
    }
}