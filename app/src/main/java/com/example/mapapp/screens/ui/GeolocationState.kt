package com.example.mapapp.screens.ui

import com.example.mapapp.data.models.GeoLocationItem

data class GeolocationState(
    val location: GeoLocationItem? = null,
    val isLoading: Boolean = false,
    val successMsg: String? = null,
    val errorMsg: String? = null

)