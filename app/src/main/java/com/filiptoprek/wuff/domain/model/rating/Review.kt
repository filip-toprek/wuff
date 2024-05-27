package com.filiptoprek.wuff.domain.model.rating

import java.time.LocalDate

data class Review(
    val rating: Int = 0,
    val userId: String = "",
    val dateOfReview: String = "",
    val walkId: String = "",
    val walkerId: String = "",
)