package com.kadircelep.minimalistweather.weather.platform

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

open class CoroutineDispatchers {
    open val Main: CoroutineDispatcher by lazy { Dispatchers.Main }
    open val IO: CoroutineDispatcher by lazy { Dispatchers.IO }
}