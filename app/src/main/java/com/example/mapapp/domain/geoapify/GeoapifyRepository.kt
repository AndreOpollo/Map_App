package com.example.mapapp.domain.geoapify

import com.example.mapapp.data.models.geoapify.Geometry
import com.example.mapapp.util.Resource
import kotlinx.coroutines.flow.Flow

interface GeoapifyRepository {
    suspend fun getRoutePoints(points:String): Flow<Resource<List<Geometry>>>
}