package com.kadircelep.minimalistweather.weather.presentation

import androidx.annotation.VisibleForTesting
import com.kadircelep.minimalistweather.R
import com.kadircelep.minimalistweather.weather.WeatherContract
import com.kadircelep.minimalistweather.weather.platform.CoroutineDispatchers
import com.kadircelep.minimalistweather.weather.ui.ErrorUiModel
import com.kadircelep.minimalistweather.weather.ui.UiAction
import com.kadircelep.minimalistweather.weather.ui.UiState
import com.kadircelep.minimalistweather.weather.ui.WeatherUiModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.CancellationException
import kotlin.properties.Delegates

class WeatherPresenter(
    private val interactor: WeatherContract.Interactor,
    dispatchers: CoroutineDispatchers = CoroutineDispatchers()
) : WeatherContract.Presenter {

//    private val uiScope = CoroutineScope(dispatchers.Main)
    private val ioScope = CoroutineScope(dispatchers.IO)

    @VisibleForTesting
    var state: UiState by Delegates.observable(UiState.Loading,
        { _, _: UiState, new: UiState ->
            view.onState(new)
        })

    private lateinit var view: WeatherContract.View

    override fun attach(viewToAttach: WeatherContract.View) {
        view = viewToAttach
    }

    override fun detach() {
        ioScope.cancel(CancellationException("View is detached. Cancelling presentation tasks on the IO thread."))
    }

    override fun onUiAction(uiAction: UiAction) {
            when (uiAction) {
                is UiAction.LocationUpdated -> updateWeather(uiAction.latitude, uiAction.longitude)
                is UiAction.RetryClicked -> updateWeather(uiAction.latitude, uiAction.longitude)
                UiAction.LocationPermissionDenied -> state = UiState.Error(locationPermissionError)
                UiAction.LocationPermissionGranted -> onPermissionGranted()
            }
    }

    private fun onPermissionGranted() {
        state = UiState.Loading
        state = UiState.RefreshLocation
    }

    private fun updateWeather(latitude: Double, longitude: Double) {
        state = UiState.Loading

        ioScope.launch {
            try {
                refreshForecast(latitude, longitude)
            } catch (exception: Exception) {
                state = UiState.Error(
                    ErrorUiModel(R.string.something_went_wrong, R.string.retry) {
                        onUiAction(UiAction.RetryClicked(latitude, longitude))
                    }
                )
            }
        }
    }

    private fun refreshForecast(latitude: Double, longitude: Double) {
        val forecast = interactor.getForecast(latitude, longitude)
        state = if (forecast != null) {
            UiState.Content(
                WeatherUiModel(
                    Date().toString(),
                    forecast.summary
                )
            )
        } else {
            UiState.Error(
                ErrorUiModel(R.string.something_went_wrong, R.string.retry) {
                    // This is a shortcut to avoid persisting latest request
                    // Ideally a PrimaryActionClicked event should be fired.
                    onUiAction(UiAction.RetryClicked(latitude, longitude))
                })
        }
    }

    private val locationPermissionError: ErrorUiModel = ErrorUiModel(
        R.string.permission_denied_message_location,
        R.string.permission_denied_cta_location
    ) { state = UiState.RequestLocationPermission }

}
