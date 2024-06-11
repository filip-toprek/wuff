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
            val snapshot = firebaseFirestore.collection("users")
                .get()
                .await()

            snapshot.documents
                .filter { document ->
                    val walker = document["walker"] as? Map<*, *>
                    val isApproved = walker?.get("approved") as? Boolean ?: false
                    val city = document["city"] as? String ?: ""
                    if(user == null)
                    {
                        walker != null && isApproved

                    }else
                    {
                        walker != null && isApproved && city == user.city
                    }
                }
                .map { document ->
                    document.toObject(UserProfile::class.java)
                }
        } catch (e: Exception) {
            emptyList()
        }
    }
}