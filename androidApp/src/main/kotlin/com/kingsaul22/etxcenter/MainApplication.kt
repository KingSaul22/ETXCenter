package com.kingsaul22.etxcenter

import android.app.Application
import com.kingsaul22.etxcenter.core.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // This only runs ONCE for the entire lifespan of the app
        initKoin {
            androidLogger()
            androidContext(this@MainApplication)
        }
    }
}