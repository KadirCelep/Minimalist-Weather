package com.kadircelep.minimalistweather.weather

import com.kadircelep.minimalistweather.weather.domain.Forecast
import com.kadircelep.minimalistweather.weather.platform.TestDispatchers
import com.kadircelep.minimalistweather.weather.presentation.WeatherPresenter
import com.kadircelep.minimalistweather.weather.ui.UiAction
import com.kadircelep.minimalistweather.weather.ui.UiState
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.util.*

class WeatherPresenterTest {
    @Mock
    private lateinit var view: WeatherContract.View
    @Mock
    private lateinit var interactor: WeatherContract.Interactor

    private lateinit var presenter: WeatherPresenter

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        presenter = WeatherPresenter(
            interactor,
            TestDispatchers()
        )
        presenter.attach(view)
    }

    @After
    fun tearDown() {
        presenter.detach()
    }

    @Test
    fun `when permission is denied, set state to Error`() {
        presenter.onUiAction(UiAction.LocationPermissionDenied)
        val actual = presenter.state
        Assert.assertEquals(UiState.Error::class.java, actual.javaClass)
    }

    @Test
    fun `when permission is granted, set state to RefreshLocation`() {
        presenter.onUiAction(UiAction.LocationPermissionGranted)
        val actual = presenter.state
        Assert.assertEquals(UiState.RefreshLocation::class.java, actual.javaClass)
    }

    @Test
    fun `given a new location, when there's a valid Forecast response for it, state should be a Content`() {
        `when`(
            interactor.getForecast(
                0.0,
                0.0
            )
        ).thenReturn(stubForecast)

        presenter.onUiAction(UiAction.LocationUpdated(0.0, 0.0))
        val actual = presenter.state
        Assert.assertEquals(UiState.Content::class.java, actual.javaClass)
    }

    @Test
    fun `given a new location, when there's an exception in response, state should be a Error`() {
        `when`(
            interactor.getForecast(0.0, 0.0)
        ).thenReturn(null)

        presenter.onUiAction(UiAction.LocationUpdated(0.0, 0.0))
        val actual = presenter.state
        Assert.assertEquals(UiState.Error::class.java, actual.javaClass)
    }

    private val stubForecast
        get() = Forecast(
            Date(0), "Clear", 20.0
        )
}