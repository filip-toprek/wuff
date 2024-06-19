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
        // Update the Firestore document with the new location data
        firebaseFirestore.collection("locations").document(location.userId).update(
            "longitude", location.longitude,
            "latitude", location.latitude
        ).await()
    }

    override suspend fun getLocationPoints(walkerId: String, reservationId: String): List<Location> {
        return try {
            val userDocument = firebaseFirestore.collection("locations").document(walkerId)
            // Run a Firestore transaction to retrieve the location points
            firebaseFirestore.runTransaction { transaction ->
                val documentSnapshot = transaction.get(userDocument)
                // Retrieve the list of locations for the specified reservationId
                documentSnapshot.get(reservationId) as? List<Location> ?: emptyList()
            }.await()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun sendWalkLocationPoint(locationPoint: LocationPoint) {
        val userDocument = firebaseFirestore.collection("locations").document(locationPoint.location.userId)
        try {
            // Run a Firestore transaction to add the new location point
            firebaseFirestore.runTransaction { transaction ->
                val documentSnapshot = transaction.get(userDocument)
                // Retrieve the existing array of location points
                val array = documentSnapshot.get(locationPoint.reservationId) as? List<Location>
                // Calculate the updated array by adding the new location point
                val updatedArray = array?.plus(locationPoint.location) ?: listOf(locationPoint.location)
                // Update the Firestore document with the new array
                transaction.update(userDocument, locationPoint.reservationId, updatedArray)
            }.await()
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle exception as needed
        }
    }

    override suspend fun getLocation(userId: String): Location {
        return try {
            // Retrieve the location document from Firestore and convert it to a Location object
            firebaseFirestore.collection("locations").document(userId).get().await().toObject(Location::class.java)!!
        } catch (e: Exception) {
            e.printStackTrace()
            Location() // Return a default Location object in case of an exception
        }
    }
}