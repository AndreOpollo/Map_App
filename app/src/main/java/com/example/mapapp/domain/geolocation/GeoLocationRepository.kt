package com.example.mapapp.domain.geolocation

import com.example.mapapp.data.models.geolocation.GeoLocationItem
import com.example.mapapp.util.Resource
import kotlinx.coroutines.flow.Flow

interface GeoLocationRepository{
    suspend fun getLocationByCity(city:String): Flow<Resource<GeoLocationItem>>
}