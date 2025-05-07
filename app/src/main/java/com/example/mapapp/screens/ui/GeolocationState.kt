package com.example.mapapp.screens.ui

import com.example.mapapp.data.models.geoapify.Geometry
import com.example.mapapp.data.models.geolocation.GeoLocationItem
import com.mapbox.geojson.Point

data class GeolocationState(
    val location: GeoLocationItem? = null,
    val isLoading: Boolean = false,
    val successMsg: String? = null,
    val errorMsg: String? = null,
    val routes: List<Point>? = emptyList<Point>()

)