package com.example.arcompose.viewModel

import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel

import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    private var _currentLocation = mutableStateOf(LatLng(0.0, 0.0))
    val currentLocation = _currentLocation

    private var _zoomLevel = mutableFloatStateOf(17f)
    val zoomLevel = _zoomLevel

    private var _deviceLocation = mutableStateOf(LatLng(0.0, 0.0))
    val deviceLocation = _deviceLocation






}

