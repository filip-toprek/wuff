package com.filiptoprek.wuff.domain.model.location

import kotlin.math.*

data class Location(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val userId: String = ""
){
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371000.0 // Radius of the Earth in meters

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadius * c
    }

    fun isWithinProximity(walkerLocation: Location, userLocation: Location, proximity: Double): Boolean {
        val distance = calculateDistance(
            walkerLocation.latitude, walkerLocation.longitude,
            userLocation.latitude, userLocation.longitude
        )
        return distance <= proximity
    }
}

