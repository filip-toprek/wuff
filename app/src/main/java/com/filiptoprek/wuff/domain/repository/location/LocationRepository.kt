package com.filiptoprek.wuff.domain.repository.location

import com.filiptoprek.wuff.domain.model.auth.Resource
import com.filiptoprek.wuff.domain.model.location.Location

interface LocationRepository {
    suspend fun sendLocation(location: Location)
    suspend fun getLocation(userId: String): Location
}