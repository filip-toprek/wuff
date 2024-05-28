package com.filiptoprek.wuff.presentation.shared

import androidx.lifecycle.ViewModel
import com.filiptoprek.wuff.domain.model.profile.UserProfile
import com.filiptoprek.wuff.domain.model.reservation.Reservation

class SharedViewModel : ViewModel() {
    var userProfile: UserProfile? = null
    var selectedReservation: Reservation? = null
    var reservationToRate: Reservation? = null
    var selectedWalker: UserProfile? = null
}