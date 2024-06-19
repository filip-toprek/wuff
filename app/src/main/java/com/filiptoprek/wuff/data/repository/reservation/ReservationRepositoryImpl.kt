package com.filiptoprek.wuff.data.repository.reservation

import android.content.Intent
import android.util.Log
import com.filiptoprek.wuff.data.utils.await
import com.filiptoprek.wuff.domain.model.auth.Resource
import com.filiptoprek.wuff.domain.model.profile.UserProfile
import com.filiptoprek.wuff.domain.model.reservation.Reservation
import com.filiptoprek.wuff.domain.model.reservation.WalkType
import com.filiptoprek.wuff.domain.repository.reservation.ReservationRepository
import com.filiptoprek.wuff.service.LocationService
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes

class ReservationRepositoryImpl @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
) : ReservationRepository {

    override suspend fun createReservation(reservation: Reservation): Resource<Unit> {
        return try {
            // Retrieve the user's profile document from Firestore
            val userProfile = firebaseFirestore.collection("users")
                .document(reservation.userId)
                .get()
                .await()
                .toObject(UserProfile::class.java)

            // Check if user balance is sufficient
            if (userProfile?.balance!! < reservation.price ||
                userProfile.balance.minus(userProfile.pendingBalance.plus(reservation.price)) <= 0) {
                return Resource.Failure(Exception("Insufficient balance"))
            }

            // Update the user's pending balance
            firebaseFirestore.collection("users").document(reservation.userId)
                .update("pendingBalance", BigDecimal(userProfile.pendingBalance.plus(reservation.price))
                    .setScale(2, RoundingMode.HALF_EVEN).toDouble()).await()

            // Create the reservation
            firebaseFirestore.collection("reservations").document().set(reservation).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }

    override suspend fun deleteReservation(reservation: Reservation): Resource<Unit> {
        return try {
            // Retrieve the user's profile document from Firestore
            val userProfile = firebaseFirestore.collection("users")
                .document(reservation.userId)
                .get()
                .await()
                .toObject(UserProfile::class.java)!!

            // Update the user's pending balance
            firebaseFirestore.collection("users").document(reservation.userId)
                .update("pendingBalance", userProfile.pendingBalance.minus(reservation.price)).await()

            // Delete the reservation
            firebaseFirestore.collection("reservations").document(reservation.reservationId).delete().await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }

    override suspend fun acceptReservation(reservationId: String): Resource<Unit> {
        return try {
            // Update the reservation to mark it as accepted
            firebaseFirestore.collection("reservations").document(reservationId)
                .update("accepted", true).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }

    override suspend fun startWalk(reservationId: String): Resource<Unit> {
        return try {
            // Update the reservation to mark the walk as started
            firebaseFirestore.collection("reservations").document(reservationId)
                .update("started", true).await()

            // Update the time the walk started
            firebaseFirestore.collection("reservations").document(reservationId)
                .update("timeWalkStarted", Timestamp.now()).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }

    private suspend fun transferCoins(reservation: Reservation) {
        try {
            // Retrieve user and walker profiles from Firestore
            val userProfile = firebaseFirestore.collection("users")
                .document(reservation.userId)
                .get()
                .await()
                .toObject(UserProfile::class.java)

            val walkerProfile = firebaseFirestore.collection("users")
                .document(reservation.walkerUserId)
                .get()
                .await()
                .toObject(UserProfile::class.java)

            // Update the balances and pending balances
            firebaseFirestore.collection("users").document(reservation.userId).update(
                "balance", BigDecimal(userProfile?.balance?.minus(reservation.price)!!)
                    .setScale(2, RoundingMode.HALF_EVEN).toDouble()
            ).await()

            firebaseFirestore.collection("users").document(reservation.walkerUserId).update(
                "balance", BigDecimal(walkerProfile?.balance?.plus(reservation.price)!!)
                    .setScale(2, RoundingMode.HALF_EVEN).toDouble()
            ).await()

            firebaseFirestore.collection("users").document(reservation.userId).update(
                "pendingBalance", userProfile.pendingBalance.minus(reservation.price)
            ).await()

            // Update the number of walks for both user and walker
            firebaseFirestore.collection("users").document(reservation.walkerUserId).update(
                "numOfWalks", walkerProfile.numOfWalks.plus(1)
            ).await()

            firebaseFirestore.collection("users").document(reservation.userId).update(
                "numOfWalks", userProfile.numOfWalks.plus(1)
            ).await()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun endWalk(reservation: Reservation): Resource<Unit> {
        return try {
            // Update the reservation to mark the walk as ended
            firebaseFirestore.collection("reservations").document(reservation.reservationId)
                .update("timeWalkEnded", Timestamp.now()).await()

            // Update the reservation to mark it as completed
            firebaseFirestore.collection("reservations").document(reservation.reservationId)
                .update("completed", true).await()

            // Transfer the coins between user and walker
            transferCoins(reservation)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }

    override suspend fun declineReservation(reservation: Reservation): Resource<Unit> {
        return try {
            // Update the reservation to mark it as declined
            firebaseFirestore.collection("reservations").document(reservation.reservationId)
                .update("declined", true).await()

            // Retrieve the user's profile document from Firestore
            val userProfile = firebaseFirestore.collection("users")
                .document(reservation.userId)
                .get()
                .await()
                .toObject(UserProfile::class.java)!!

            // Update the user's pending balance
            firebaseFirestore.collection("users").document(reservation.userId)
                .update("pendingBalance", userProfile.pendingBalance.minus(reservation.price)).await()

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }

    override suspend fun getReservationsForWalker(userId: String): List<Reservation> {
        return try {
            // Retrieve the reservations collection from Firestore
            val snapshot = firebaseFirestore.collection("reservations")
                .get()
                .await()

            // Filter reservations for the given walker ID
            snapshot.documents
                .filter { document ->
                    val walkerUserId = document.get("walkerUserId") as? String ?: ""
                    userId == walkerUserId
                }
                .map { document ->
                    val reservation = document.toObject(Reservation::class.java)
                    reservation?.reservationId = document.id
                    reservation!!
                }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getReservations(userId: String): List<Reservation> {
        return try {
            // Retrieve the reservations collection from Firestore
            val snapshot = firebaseFirestore.collection("reservations")
                .get()
                .await()

            // Filter reservations for the given user ID
            snapshot.documents
                .filter { document ->
                    val getUserId = document.get("userId") as? String ?: ""
                    userId == getUserId
                }
                .map { document ->
                    val reservation = document.toObject(Reservation::class.java)
                    reservation?.reservationId = document.id
                    reservation!!
                }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getWalkTypes(): List<WalkType> {
        return try {
            // Retrieve the walk types collection from Firestore
            val snapshot = firebaseFirestore.collection("walks")
                .get()
                .await()

            // Convert documents to WalkType objects
            snapshot.documents
                .map { document ->
                    document.toObject(WalkType::class.java)!!
                }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getStartedReservationByWalkerId(walkerId: String): Reservation {
        return try {
            // Query Firestore for reservations that are started but not completed
            val snapshot = firebaseFirestore.collection("reservations")
                .whereEqualTo("walkerUserId", walkerId)
                .whereEqualTo("started", true)
                .whereEqualTo("completed", false)
                .get()
                .await()

            // Retrieve the first result and set the reservation ID
            val result = snapshot.documents.firstOrNull()?.toObject(Reservation::class.java)!!
            result.reservationId = snapshot.documents.firstOrNull()?.id.toString()
            result
        } catch (e: Exception) {
            Reservation()
        }
    }

    override suspend fun updateWalkDistance(reservationId: String, distance: Double) {
        try {
            // Update the distance for the given reservation ID
            firebaseFirestore.collection("reservations").document(reservationId)
                .update("distance", distance).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}