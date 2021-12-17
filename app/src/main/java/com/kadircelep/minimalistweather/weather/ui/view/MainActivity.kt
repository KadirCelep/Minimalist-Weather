package com.kadircelep.minimalistweather.weather.ui.view

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.kadircelep.minimalistweather.App
import com.kadircelep.minimalistweather.R
import com.kadircelep.minimalistweather.weather.domain.WeatherInteractor
import com.kadircelep.minimalistweather.weather.WeatherContract
import com.kadircelep.minimalistweather.weather.api.WeatherService
import com.kadircelep.minimalistweather.weather.presentation.WeatherPresenter
import com.kadircelep.minimalistweather.weather.ui.ErrorUiModel
import com.kadircelep.minimalistweather.weather.ui.UiAction
import com.kadircelep.minimalistweather.weather.ui.UiState
import com.kadircelep.minimalistweather.weather.ui.WeatherUiModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), WeatherContract.View {
    val main = CoroutineScope(Dispatchers.Main)
    //TODO: Ideally provided by dependency injection
    private val presenter: WeatherContract.Presenter = WeatherPresenter(
        WeatherInteractor(WeatherService(App.instance.okHttpClient))
    )

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        presenter.attach(this)

        //TODO: Instead, have an initial state, provided by the presenter.
        checkLocationPermission()
    }

    override fun onDestroy() {
        presenter.detach()
        super.onDestroy()
    }

    override fun onState(uiState: UiState) {
        main.launch {
            when (uiState) {
                UiState.Loading -> showLoading()
                UiState.RequestLocationPermission -> requestLocationPermission()
                UiState.RefreshLocation -> refreshUserLocation()
                is UiState.Content -> showContent(uiState.uiModel)
                is UiState.Error -> showError(uiState.errorUiModel)
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            LocationPermissionRequestCode -> if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                //TODO: Ideally also check "shouldShowRequestPermissionRationale"
                presenter.onUiAction(UiAction.LocationPermissionGranted)
            } else {
                presenter.onUiAction(UiAction.LocationPermissionDenied)
            }
            else -> throw Exception("No permissions were requested with code $requestCode.")
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            presenter.onUiAction(UiAction.LocationPermissionDenied)
        } else {
            refreshUserLocation()
        }
    }

    private fun refreshUserLocation() {
        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                //TODO: Still send an action to presenter even if the location is null.
                location?.let {
                    presenter.onUiAction(
                        UiAction.LocationUpdated(
                            location.latitude,
                            location.longitude
                        )
                    )
                }
            }
        } else {
            presenter.onUiAction(UiAction.LocationPermissionDenied)
        }

    }

    private fun showLoading() {
        labelPrimary.visibility = View.GONE
        labelSecondary.visibility = View.GONE
        button.visibility = View.GONE
        loading.visibility = View.VISIBLE
    }

    private fun showContent(uiModel: WeatherUiModel) {
        loading.visibility = View.GONE
        with(labelPrimary) {
            visibility = View.VISIBLE
            text = uiModel.temperature
        }

        with(labelSecondary) {
            visibility = View.VISIBLE
            text = uiModel.lastUpdated
        }

        with(button) {
            visibility = View.VISIBLE
            text = getString(R.string.refresh)
            // This click listener is ideally provided as part of the model
            setOnClickListener { refreshUserLocation() }
        }
    }

    private fun showError(errorUiModel: ErrorUiModel) {
        loading.visibility = View.GONE
        with(labelPrimary) {
            visibility = View.VISIBLE
            text = getString(R.string.error)
        }

        with(labelSecondary) {
            visibility = View.VISIBLE
            text = getString(errorUiModel.message)
        }

        with(button) {
            visibility = View.VISIBLE
            text = getString(errorUiModel.actionMessage)
            setOnClickListener { errorUiModel.action.invoke() }
        }
    }

    private fun requestLocationPermission() {
        requestPermissions(
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
            LocationPermissionRequestCode
        )
    }

    companion object {
        private const val LocationPermissionRequestCode = 101
    }
}
