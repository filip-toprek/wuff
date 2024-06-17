package com.filiptoprek.wuff.domain.repository.location

import com.filiptoprek.wuff.domain.model.auth.Resource
import com.filiptoprek.wuff.domain.model.location.Location
import com.filiptoprek.wuff.domain.model.location.LocationPoint

interface LocationRepository {
    suspend fun sendLocation(location: Location)
    suspend fun sendWalkLocationPoint(locationPoint: LocationPoint)
    suspend fun getLocation(userId: String): Location
    suspend fun getLocationPoints(walkerId: String, reservationId: String): List<Location>
}