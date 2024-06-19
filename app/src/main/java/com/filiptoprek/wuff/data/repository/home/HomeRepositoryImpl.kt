package com.filiptoprek.wuff.data.repository.home

import com.filiptoprek.wuff.data.utils.await
import com.filiptoprek.wuff.domain.model.profile.UserProfile
import com.filiptoprek.wuff.domain.repository.home.HomeRepository
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,)
    : HomeRepository {
    override suspend fun getWalkerList(user: UserProfile?): List<UserProfile?> {
        return try {
            // Fetch the documents from the Firestore collection "users"
            val snapshot = firebaseFirestore.collection("users")
                .get()
                .await()

            // Filter and map the documents to UserProfile objects
            snapshot.documents
                .filter { document ->
                    val walker = document["walker"] as? Map<*, *>
                    val isApproved = walker?.get("approved") as? Boolean ?: false
                    val city = document["city"] as? String ?: ""

                    // Check if the walker is approved and (if user is not null) if the city matches
                    // we don't want walkers that are not in user's city
                    walker != null && isApproved && (user == null || city == user.city)
                }
                .map { document ->
                    // Convert the document to UserProfile object
                    document.toObject(UserProfile::class.java)
                }
        } catch (e: Exception) {
            // Return an empty list in case of any exception
            emptyList()
        }
    }

}