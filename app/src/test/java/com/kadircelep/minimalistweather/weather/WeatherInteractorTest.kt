package com.kadircelep.minimalistweather.weather

import com.kadircelep.minimalistweather.weather.domain.Forecast
import com.kadircelep.minimalistweather.weather.domain.WeatherInteractor
import com.squareup.okhttp.*
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class WeatherInteractorTest {
    private lateinit var interactor: WeatherInteractor

    @Mock
    private lateinit var service: WeatherContract.Service

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        interactor = WeatherInteractor(service)
    }

    @Test
    fun `when service returns an error, interactor should return null Forecast`() {
        //Given
        `when`(service.fetchForecast(0.0, 0.0))
            .thenReturn(stubErrorResponse)

        //When
        val actual: Forecast? = interactor.getForecast(0.0, 0.0)

        //Then
        assertNull(actual)
    }

    private val stubErrorResponse
        get() = Response.Builder()
            .request(
                Request.Builder()
                    .url("http://test.url")
                    .build()
            )
            .protocol(Protocol.HTTP_2)
            .code(400)
            .build()

}