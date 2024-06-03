package com.filiptoprek.wuff.data.repository.location

import com.filiptoprek.wuff.data.utils.await
import com.filiptoprek.wuff.domain.model.auth.Resource
import com.filiptoprek.wuff.domain.model.location.Location
import com.filiptoprek.wuff.domain.repository.location.LocationRepository
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
    ) : LocationRepository {
    override suspend fun sendLocation(location: Location) {
        firebaseFirestore.collection("locations").document(location.userId).set(
            hashMapOf(
                "longitude" to location.longitude,
                "latitude" to location.latitude
            )
        )
    }

    override suspend fun getLocation(walkerId: String): Location {
       return try {
           firebaseFirestore.collection("locations").document(walkerId).get().await().toObject(Location::class.java)!!
       }catch (e: Exception)
       {
           Location()
       }
    }
}