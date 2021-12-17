package com.kadircelep.minimalistweather.weather.platform

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.Unconfined

class TestDispatchers : CoroutineDispatchers() {
    override val Main: CoroutineDispatcher = Unconfined
    override val IO: CoroutineDispatcher = Unconfined
}
