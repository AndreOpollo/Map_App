package com.example.mapapp.util

import android.content.Context
import com.example.mapapp.R

object Constants {
    // This needs to be initialized once with context
    lateinit var OPENWEATHER_API_KEY: String
        private set
    lateinit var GEOAPIFY_API_KEY: String
         private set

    fun initialize(context: Context) {
        OPENWEATHER_API_KEY = context.getString(R.string.openweather_api_key)
        GEOAPIFY_API_KEY = context.getString(R.string.geoapify_api_key)
    }
}