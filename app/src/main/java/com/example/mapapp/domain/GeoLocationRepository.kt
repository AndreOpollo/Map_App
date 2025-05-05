package com.example.mapapp.domain

import com.example.mapapp.data.GeoLocationApi
import com.example.mapapp.data.models.GeoLocation
import com.example.mapapp.data.models.GeoLocationItem
import com.example.mapapp.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface GeoLocationRepository{
    suspend fun getLocationByCity(city:String): Flow<Resource<GeoLocationItem>>
}