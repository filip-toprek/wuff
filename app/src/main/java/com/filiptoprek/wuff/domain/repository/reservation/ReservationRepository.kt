package com.filiptoprek.wuff.domain.repository.reservation

import com.filiptoprek.wuff.domain.model.auth.Resource
import com.filiptoprek.wuff.domain.model.reservation.Reservation
import com.filiptoprek.wuff.domain.model.reservation.WalkType

interface ReservationRepository {
    suspend fun getWalkTypes(): List<WalkType>
    suspend fun createReservation(reservation: Reservation): Resource<Unit>
    suspend fun getReservations(userId: String): List<Reservation>
    suspend fun deleteReservations(reservationId: String): Resource<Unit>
}