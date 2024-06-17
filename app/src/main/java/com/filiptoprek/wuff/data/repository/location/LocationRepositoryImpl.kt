package com.filiptoprek.wuff.data.repository.location

import android.R.attr.data
import com.filiptoprek.wuff.data.utils.await
import com.filiptoprek.wuff.domain.model.location.Location
import com.filiptoprek.wuff.domain.model.location.LocationPoint
import com.filiptoprek.wuff.domain.repository.location.LocationRepository
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.getField
import javax.inject.Inject


class LocationRepositoryImpl @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
    ) : LocationRepository {
    override suspend fun sendLocation(location: Location) {
        firebaseFirestore.collection("locations").document(location.userId).update(
                "longitude", location.longitude,
                "latitude", location.latitude
        ).await()
    }

    override suspend fun getLocationPoints(walkerId: String, reservationId: String): List<Location> {
        return try {
            val userDocument = firebaseFirestore.collection("locations").document(walkerId)
            firebaseFirestore.runTransaction { transaction ->
                val documentSnapshot = transaction.get(userDocument)

                documentSnapshot.get(reservationId) as? List<Location>
            }.await()
        }catch (e: Exception)
        {
            e.printStackTrace()
            emptyList<Location>()
        }
    }
    override suspend fun sendWalkLocationPoint(locationPoint: LocationPoint) {
        val userDocument = firebaseFirestore.collection("locations").document(locationPoint.location.userId)

        try {
            firebaseFirestore.runTransaction { transaction ->
                val documentSnapshot = transaction.get(userDocument)

                // Retrieve the array from the document
                val array = documentSnapshot.get(locationPoint.reservationId) as? List<Location>

                // Calculate the updated array
                val updatedArray = array?.plus(locationPoint.location)
                    ?: listOf(locationPoint.location)  // If array is null or empty, create a new list

                // Update the document with the new array
                transaction.update(userDocument, locationPoint.reservationId, updatedArray)
            }.await()
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle exception as needed
        }
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