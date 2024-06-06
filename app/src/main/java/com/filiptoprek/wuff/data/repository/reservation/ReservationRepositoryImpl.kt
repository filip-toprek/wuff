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
            val user = firebaseFirestore.collection("users").document(reservation.userId).get().await().toObject(UserProfile::class.java)
            if(user?.balance!! < reservation.price || user.balance.minus(user.pendingBalance.plus(reservation.price)) <= 0)
            {
                return Resource.Failure(Exception("Nedovljan saldo"))
            }
            firebaseFirestore.collection("users").document(reservation.userId).update("pendingBalance", BigDecimal(user.pendingBalance.plus(reservation.price)).setScale(2, RoundingMode.HALF_EVEN).toDouble()).await()
            firebaseFirestore.collection("reservations").document().set(reservation).await()
            Resource.Success(Unit)
        }catch (e: Exception){
            return Resource.Failure(e)
        }
    }

    override suspend fun deleteReservations(reservationId: String): Resource<Unit> {
        return try {
            firebaseFirestore.collection("reservations").document(reservationId)
                .delete().await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }

    override suspend fun acceptReservations(reservationId: String): Resource<Unit> {
        return try {
            firebaseFirestore.collection("reservations").document(reservationId)
                .update("accepted", true).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }

    override suspend fun startWalk(reservationId: String): Resource<Unit> {
        return try {
            firebaseFirestore.collection("reservations").document(reservationId)
                .update("started", true).await()
            firebaseFirestore.collection("reservations").document(reservationId)
                .update("timeWalkStarted", Timestamp.now()).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }

    private suspend fun transferCoins(reservation: Reservation)
    {
        try {
            val user = firebaseFirestore.collection("users").document(reservation.userId).get().await().toObject(UserProfile::class.java)
            val walker = firebaseFirestore.collection("users").document(reservation.walkerUserId).get().await().toObject(UserProfile::class.java)

            firebaseFirestore.collection("users").document(reservation.userId).update("balance",
                BigDecimal(user?.balance?.minus(reservation.price)!!).setScale(2, RoundingMode.HALF_EVEN).toDouble()
            ).await()

            firebaseFirestore.collection("users").document(reservation.walkerUserId).update("balance",
                BigDecimal(walker?.balance?.plus(reservation.price)!!).setScale(2, RoundingMode.HALF_EVEN).toDouble(),
            ).await()

            firebaseFirestore.collection("users").document(reservation.userId).update("pendingBalance", user.pendingBalance.minus(reservation.price)).await()
            firebaseFirestore.collection("users").document(reservation.walkerUserId).update("numOfWalks", walker.numOfWalks.plus(1)).await()
            firebaseFirestore.collection("users").document(reservation.userId).update("numOfWalks", user.numOfWalks.plus(1)).await()

        }catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    override suspend fun endWalk(reservation: Reservation): Resource<Unit> {
        return try {
            firebaseFirestore.collection("reservations").document(reservation.reservationId)
                .update("completed", true).await()

            firebaseFirestore.collection("reservations").document(reservation.reservationId)
                .update("timeWalkEnded", Timestamp.now()).await()

            val user = firebaseFirestore.collection("users").document(reservation.userId).get().await().toObject(UserProfile::class.java)!!
            firebaseFirestore.collection("users").document(reservation.userId).update("pendingBalance", user.pendingBalance.minus(reservation.price)).await()


            transferCoins(reservation)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }

    override suspend fun declineReservations(reservationId: String): Resource<Unit> {
        return try {
            firebaseFirestore.collection("reservations").document(reservationId)
                .update("declined", true).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }

    override suspend fun getReservationsForWalker(userId: String): List<Reservation> {
        return try {
            val snapshot = firebaseFirestore.collection("reservations")
                .get()
                .await()

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
            val snapshot = firebaseFirestore.collection("reservations")
                .get()
                .await()

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
            val snapshot = firebaseFirestore.collection("walks")
                .get()
                .await()

            snapshot.documents
                .map { document ->
                    document.toObject(WalkType::class.java)!!
                }
        } catch (e: Exception) {
            emptyList()
        }
    }
}