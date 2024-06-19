package com.filiptoprek.wuff.data.repository.rating

import com.filiptoprek.wuff.data.utils.await
import com.filiptoprek.wuff.domain.model.auth.Resource
import com.filiptoprek.wuff.domain.model.profile.Transaction
import com.filiptoprek.wuff.domain.model.profile.UserProfile
import com.filiptoprek.wuff.domain.model.rating.Review
import com.filiptoprek.wuff.domain.model.reservation.Reservation
import com.filiptoprek.wuff.domain.repository.rating.RatingRepository
import com.google.firebase.firestore.FirebaseFirestore
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.UUID
import javax.inject.Inject

class RatingRepositoryImpl  @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
) : RatingRepository {
    override suspend fun getWalkerReviews(userId: String): List<Review> {
        return try {
            // Retrieve the user's profile document from Firestore
            val userProfile = firebaseFirestore.collection("users").document(userId)
                .get()
                .await()
                .toObject(UserProfile::class.java)

            // Return the list of reviews or an empty list if null
            userProfile?.walker?.reviews ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace() // Log the exception
            emptyList()
        }
    }

    override suspend fun rateUser(review: Review): Resource<Unit> {
        return try {
            // Retrieve the current list of reviews for the walker
            val userProfile = firebaseFirestore.collection("users").document(review.walkerId)
                .get()
                .await()
                .toObject(UserProfile::class.java)

            val reviewList = userProfile?.walker?.reviews ?: emptyList()

            // Add the new review to the list
            val updatedReviewList = reviewList.plus(review)

            // Update the walker's reviews and average rating
            firebaseFirestore.collection("users").document(review.walkerId).update(
                mapOf(
                    "walker.reviews" to updatedReviewList,
                    "walker.averageRating" to BigDecimal(updatedReviewList.map { it.rating }.average())
                        .setScale(2, RoundingMode.HALF_EVEN)
                        .toDouble()
                )
            ).await()

            // Mark the reservation as rated
            firebaseFirestore.collection("reservations").document(review.walkId).update("rated", true).await()

            Resource.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace() // Log the exception
            Resource.Failure(e)
        }
    }
}