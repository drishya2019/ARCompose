package com.ai.tsLocationTracker.location

import android.location.Location

interface LocationTracker {
    suspend fun getCurrentLocation(): Location?
}