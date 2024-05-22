package com.filiptoprek.wuff.domain.model

import com.google.firebase.Timestamp

data class Reload (
    val reloadAmount: Double,
    val reloadDate: Timestamp = Timestamp.now(),
    var reloadUser: String = "",

    )