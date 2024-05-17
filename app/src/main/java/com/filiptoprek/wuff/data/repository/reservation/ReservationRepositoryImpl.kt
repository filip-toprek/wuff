package com.filiptoprek.wuff.data.repository.reservation

import com.filiptoprek.wuff.data.utils.await
import com.filiptoprek.wuff.domain.model.profile.UserProfile
import com.filiptoprek.wuff.domain.model.reservation.WalkType
import com.filiptoprek.wuff.domain.repository.reservation.ReservationRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import javax.inject.Inject

class ReservationRepositoryImpl @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore
) : ReservationRepository {
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