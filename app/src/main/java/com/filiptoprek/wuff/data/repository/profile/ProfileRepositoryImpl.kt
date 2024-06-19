package com.filiptoprek.wuff.data.repository.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arrow.fx.coroutines.Use
import com.filiptoprek.wuff.data.utils.await
import com.filiptoprek.wuff.domain.model.auth.Resource
import com.filiptoprek.wuff.domain.model.profile.UserProfile
import com.filiptoprek.wuff.domain.repository.profile.ProfileRepository
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import javax.inject.Inject


class ProfileRepositoryImpl @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
) : ProfileRepository {

    override suspend fun becomeWalker(userProfile: UserProfile, userId: String): Resource<UserProfile>? {
        return try {
            // Update the user's walker status
            firebaseFirestore.collection("users").document(userId).update("walker", userProfile.walker).await()
            // Update the dateUpdated field
            firebaseFirestore.collection("users").document(userId).update("dateUpdated", System.currentTimeMillis()).await()
            Resource.Success(userProfile)
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }

    override suspend fun updateUserCity(userId: String, city: String) {
        try {
            // Update the user's city
            firebaseFirestore.collection("users").document(userId).update("city", city).await()
        } catch (e: Exception) {
            e.printStackTrace() // Log the exception
        }
    }

    override suspend fun updateUserProfile(userProfile: UserProfile, userId: String): Resource<UserProfile> {
        return try {
            // Update the aboutUser field
            firebaseFirestore.collection("users").document(userId).update("aboutUser", userProfile.aboutUser).await()
            // Update the dateUpdated field
            firebaseFirestore.collection("users").document(userId).update("dateUpdated", System.currentTimeMillis()).await()
            Resource.Success(userProfile)
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }

    override suspend fun getUserProfile(userId: String): UserProfile? {
        return try {
            // Retrieve the user's profile document
            val result = firebaseFirestore.collection("users").document(userId).get().await()
            // Convert the document to UserProfile object if it exists
            result?.toObject<UserProfile>()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}