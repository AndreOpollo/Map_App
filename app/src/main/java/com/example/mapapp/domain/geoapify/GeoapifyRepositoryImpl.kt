package com.example.mapapp.domain.geoapify

import com.example.mapapp.data.GeoapifyApi
import com.example.mapapp.data.models.geoapify.Geometry
import com.example.mapapp.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GeoapifyRepositoryImpl @Inject constructor(
    private val api: GeoapifyApi
): GeoapifyRepository {
    override suspend fun getRoutePoints(points: String): Flow<Resource<List<Geometry>>> {
        return flow {
            try {
                emit(Resource.Loading(true))
                val response = api.getRouteCoordinates(points)
                val geometryList = response.features.map{ it.geometry }
                emit(Resource.Loading(false))
                emit(Resource.Success(data = geometryList))

            }catch (e:Exception){
                e.printStackTrace()
                emit(Resource.Loading(false))
                emit(Resource.Error(message = e.localizedMessage?:"Something went wrong"))
            }
        }
    }
}