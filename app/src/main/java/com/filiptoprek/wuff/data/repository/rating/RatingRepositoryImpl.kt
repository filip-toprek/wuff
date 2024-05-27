package com.filiptoprek.wuff.data.repository.rating

import com.filiptoprek.wuff.data.utils.await
import com.filiptoprek.wuff.domain.model.auth.Resource
import com.filiptoprek.wuff.domain.model.profile.Transaction
import com.filiptoprek.wuff.domain.model.profile.UserProfile
import com.filiptoprek.wuff.domain.model.rating.Review
import com.filiptoprek.wuff.domain.model.reservation.Reservation
import com.filiptoprek.wuff.domain.repository.rating.RatingRepository
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID
import javax.inject.Inject

class RatingRepositoryImpl  @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
) : RatingRepository {
    override suspend fun getWalkerReviews(userId: String): List<Review> {
        return try {
            val user = firebaseFirestore.collection("users").document(userId)
                .get()
                .await().toObject(UserProfile::class.java)
            user?.walker?.reviews!!
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun rateUser(review: Review): Resource<Unit> {
        val reviewList: List<Review> = firebaseFirestore.collection("users").document(review.walkerId).get().await().toObject(UserProfile::class.java)?.walker?.reviews ?: emptyList()
        return try {
            firebaseFirestore.collection("users").document(review.walkerId).update("walker.reviews", reviewList.plus(review)).await()
            val ratingList: List<Int> = reviewList.plus(review).map { it.rating }
            firebaseFirestore.collection("users").document(review.walkerId).update("walker.averageRating", ratingList.average()).await()
            firebaseFirestore.collection("reservations").document(review.walkId).update("rated", true).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }
}