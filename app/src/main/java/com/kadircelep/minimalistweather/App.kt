package com.kadircelep.minimalistweather

import android.app.Application
import com.squareup.okhttp.OkHttpClient

class App : Application() {

    // Ideally created and provided via dependency injection.
    lateinit var okHttpClient: OkHttpClient

    override fun onCreate() {
        super.onCreate()
        instance = this
        okHttpClient = OkHttpClient()
    }

    companion object {
        lateinit var instance: App
            private set
    }
}