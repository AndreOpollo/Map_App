package com.example.mapapp.di

import com.example.mapapp.domain.geoapify.GeoapifyRepository
import com.example.mapapp.domain.geoapify.GeoapifyRepositoryImpl
import com.example.mapapp.domain.geolocation.GeoLocationRepository
import com.example.mapapp.domain.geolocation.GeoLocationRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindGeoLocationRepository(
        geoLocationRepositoryImpl: GeoLocationRepositoryImpl
    ): GeoLocationRepository

    @Binds
    @Singleton
    abstract fun bindGeoApifyRepository(
        geoapifyRepositoryImpl: GeoapifyRepositoryImpl
    ): GeoapifyRepository
}