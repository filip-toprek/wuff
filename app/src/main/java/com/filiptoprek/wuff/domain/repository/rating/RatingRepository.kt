package com.filiptoprek.wuff.domain.repository.rating

import com.filiptoprek.wuff.domain.model.auth.Resource
import com.filiptoprek.wuff.domain.model.rating.Review

interface RatingRepository {
    suspend fun getWalkerReviews(userId: String): List<Review>
    suspend fun rateUser(review: Review): Resource<Unit>
}