package com.example.mapapp.di

import com.example.mapapp.domain.GeoLocationRepository
import com.example.mapapp.domain.GeoLocationRepositoryImpl
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
}