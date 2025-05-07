package com.example.mapapp.data

import com.example.mapapp.data.models.geoapify.Geoapify
import com.example.mapapp.data.models.geoapify.Geometry
import com.example.mapapp.util.Constants
import retrofit2.http.GET
import retrofit2.http.Query

interface GeoapifyApi {

    @GET("/v1/routing")
    suspend fun getRouteCoordinates(
        @Query("waypoints")points:String,
        @Query("mode")mode:String = "drive",
        @Query("apiKey")apiKey:String = Constants.GEOAPIFY_API_KEY
    ): Geoapify
}