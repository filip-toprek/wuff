package com.filiptoprek.wuff.data.repository.reservation

import com.filiptoprek.wuff.data.utils.await
import com.filiptoprek.wuff.domain.model.auth.Resource
import com.filiptoprek.wuff.domain.model.profile.UserProfile
import com.filiptoprek.wuff.domain.model.reservation.Reservation
import com.filiptoprek.wuff.domain.model.reservation.WalkType
import com.filiptoprek.wuff.domain.repository.reservation.ReservationRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import javax.inject.Inject

class ReservationRepositoryImpl @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
) : ReservationRepository {

    override suspend fun createReservation(reservation: Reservation): Resource<Unit> {
        return try {
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