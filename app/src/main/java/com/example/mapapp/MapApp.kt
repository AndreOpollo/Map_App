package com.example.mapapp

import android.app.Application
import com.example.mapapp.util.Constants
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MapApp: Application() {
    override fun onCreate() {
        super.onCreate()
        Constants.initialize(this)
    }
}