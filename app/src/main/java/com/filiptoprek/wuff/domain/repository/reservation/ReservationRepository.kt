package com.filiptoprek.wuff.domain.repository.reservation

import com.filiptoprek.wuff.domain.model.reservation.WalkType

interface ReservationRepository {
    suspend fun getWalkTypes(): List<WalkType>
}