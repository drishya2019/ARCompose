package com.example.arcompose.di

import android.app.Application
import android.content.Context
import com.ai.tsLocationTracker.location.DefaultLocationTracker
import com.ai.tsLocationTracker.location.LocationTracker
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun providesFusedLocationProviderClient(
        application: Application
    ): FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(application)

    @Provides
    @Singleton
    fun providesLocationTracker(
        fusedLocationProviderClient: FusedLocationProviderClient,
        application: Application
    ): LocationTracker = DefaultLocationTracker(
        fusedLocationProviderClient = fusedLocationProviderClient,
        application = application
    )

    @Provides
    @Singleton
    fun provideContext(
        application: Application
    ): Context = application.applicationContext

//    @Provides
//    @Singleton
//    fun provideUSerStore(
//        application: Application,
//    ): UserStore = UserStore(application.applicationContext)
}