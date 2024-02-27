package com.example.arcompose.viewModel

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ai.tsLocationTracker.location.LocationTracker
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val locationTracker: LocationTracker,
    private val fusedLocationProviderClient: FusedLocationProviderClient,

    ) : ViewModel() {

    private var _latLocation = mutableStateOf("0.0")
    val latLocation = _latLocation

    private var _lngLocation = mutableStateOf("0.0")
    val lngLocation = _lngLocation


    private var currentLocation by mutableStateOf<Location?>(null)

    val trackerEvent = MutableSharedFlow<LocationUpdates>()

    init {
        getCurrentLocation()
    }

    fun getCurrentLocation() {
        viewModelScope.launch(Dispatchers.IO) {
            currentLocation = locationTracker.getCurrentLocation() // Location

            _latLocation.value = currentLocation?.latitude.toString()
            _lngLocation.value = currentLocation?.longitude.toString()

            Log.i(
                "location  ---   LAT LNG  ",
                "${_latLocation.value} : ${_lngLocation.value}"
            )

        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun getRealTimeLocation(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                return@launch
            }
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    fun stopLocationTracking(){
        fusedLocationProviderClient.flushLocations()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            println("we have a new location result")
            locationResult
                ?: return //als er een result is dan prima, zo niet dan just return (elvis operator)
            for (location in locationResult.locations) {
                //  setLocationData(location = location)
                Log.i(
                    "location 00000000  LAT LNG  ",
                    "${location.latitude} : ${location.longitude}"
                )
                viewModelScope.launch(Dispatchers.IO) {
                    trackerEvent.emit(LocationUpdates.OnLocationTracker(location))
                }
            }
        }
    }

    companion object {
        @RequiresApi(Build.VERSION_CODES.S)
        val locationRequest: LocationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,100L).apply {
                setMinUpdateDistanceMeters(2F)
                setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
                setWaitForAccurateLocation(true)
            }.build()


    }

    override fun onCleared() {
        super.onCleared()
        stopLocationTracking()
    }

    sealed class LocationUpdates {
        data class OnLocationTracker(val currentLocation: Location) : LocationUpdates()
    }



}