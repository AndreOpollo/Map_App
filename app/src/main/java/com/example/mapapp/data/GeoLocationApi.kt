package com.example.mapapp.data


import android.provider.Settings.Global.getString
import com.example.mapapp.data.models.GeoLocationItem
import com.example.mapapp.util.Constants
import retrofit2.http.GET
import retrofit2.http.Query



interface GeoLocationApi {

    @GET("/geo/1.0/direct")
    suspend fun getLocationByCity(
        @Query("q") cityName:String,
        @Query("limit") limit: Int = 1,
        @Query("appid") apiKey: String = Constants.OPENWEATHER_API_KEY
    ): List<GeoLocationItem>
}