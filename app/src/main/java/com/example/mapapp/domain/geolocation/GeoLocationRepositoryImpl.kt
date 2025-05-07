package com.example.mapapp.domain.geolocation

import androidx.core.content.res.ResourcesCompat
import com.example.mapapp.data.GeoLocationApi
import com.example.mapapp.data.models.geolocation.GeoLocationItem
import com.example.mapapp.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GeoLocationRepositoryImpl @Inject constructor(
    private val api: GeoLocationApi
): GeoLocationRepository {
    override suspend fun getLocationByCity(city: String): Flow<Resource<GeoLocationItem>> {
        return flow {
            try {
                emit(Resource.Loading(true))
                val location = api.getLocationByCity(city)
                emit(Resource.Loading(false))
                emit(Resource.Success(data = location[0]))
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Resource.Loading(false))
                emit(Resource.Error(message = e.localizedMessage ?: "Something went wrong"))
            }
        }
    }
}