package com.filiptoprek.wuff.domain.model.reservation

import com.filiptoprek.wuff.domain.model.profile.UserProfile
import com.google.firebase.Timestamp
import java.sql.Time
import java.time.LocalDate
import java.time.LocalTime

data class Reservation (
    var reservationId: String = "",
    val walkerUserId: String = "",
    var userId: String = "",
    val dateOfWalk: String? = "",
    val timeOfWalk: String? = "",
    val accepted: Boolean = false,
    val completed: Boolean = false,
    val declined: Boolean = false,
    val started: Boolean = false,
    val price: Double = 0.0,
    val walkType: WalkType = WalkType(),
    var walker: UserProfile? = null,
    var user: UserProfile? = null,
    var rated: Boolean = false,
    val timeWalkStarted: Timestamp = Timestamp.now(),
    val timeWalkEnded: Timestamp = Timestamp.now()
)