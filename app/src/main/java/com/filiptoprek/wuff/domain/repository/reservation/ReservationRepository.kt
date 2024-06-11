package com.filiptoprek.wuff.domain.repository.reservation

import com.filiptoprek.wuff.domain.model.auth.Resource
import com.filiptoprek.wuff.domain.model.reservation.Reservation
import com.filiptoprek.wuff.domain.model.reservation.WalkType

interface ReservationRepository {
    suspend fun getWalkTypes(): List<WalkType>
    suspend fun createReservation(reservation: Reservation): Resource<Unit>
    suspend fun getReservations(userId: String): List<Reservation>
    suspend fun getReservationsForWalker(userId: String): List<Reservation>
    suspend fun declineReservation(reservation: Reservation): Resource<Unit>
    suspend fun acceptReservation(reservationId: String): Resource<Unit>
    suspend fun startWalk(reservationId: String): Resource<Unit>
    suspend fun endWalk(reservation: Reservation): Resource<Unit>
    suspend fun deleteReservation(reservation: Reservation): Resource<Unit>
}