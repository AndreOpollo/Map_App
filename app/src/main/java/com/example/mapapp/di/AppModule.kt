package com.example.mapapp.di

import com.example.mapapp.data.GeoLocationApi
import com.example.mapapp.data.GeoapifyApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient{
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }).build()
    }
    @Provides
    @Singleton
    fun provideGeoCodingApi(okHttpClient: OkHttpClient): GeoLocationApi{
        return Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeoLocationApi::class.java)
    }
    @Provides
    @Singleton
    fun provideGeoapifyApi(okHttpClient: OkHttpClient): GeoapifyApi{
        return Retrofit.Builder()
            .baseUrl("https://api.geoapify.com")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeoapifyApi::class.java)
    }
}