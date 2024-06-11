package com.filiptoprek.wuff.domain.usecase.reservation

import com.filiptoprek.wuff.domain.model.reservation.Reservation
import com.filiptoprek.wuff.domain.model.reservation.WalkType

class ValidateReservationUseCase {
    fun validateReservationUseCase(reservation: Reservation): Boolean {
        return !reservation.dateOfWalk.isNullOrEmpty() && !reservation.timeOfWalk.isNullOrEmpty() && reservation.walkType != WalkType()
    }
}