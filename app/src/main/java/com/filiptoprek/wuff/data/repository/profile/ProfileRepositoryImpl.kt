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
        return try{
            firebaseFirestore.collection("users").document(userId).update("walker", userProfile.walker).await()
            firebaseFirestore.collection("users").document(userId).update("dateUpdated", System.currentTimeMillis()).await()
            Resource.Success(userProfile)
        }catch (e: Exception)
        {
            Resource.Failure(e)
        }
    }

    override suspend fun updateUserCity(userId: String, city: String) {
        try {
            firebaseFirestore.collection("users").document(userId).update("city", city).await()
        }catch (e: Exception)
        {
            //
        }
    }

    override suspend fun updateUserProfile(userProfile: UserProfile, userId: String): Resource<UserProfile> {
        return try {
            firebaseFirestore.collection("users").document(userId).update("aboutUser", userProfile.aboutUser).await()
            firebaseFirestore.collection("users").document(userId).update("dateUpdated", System.currentTimeMillis()).await()
            Resource.Success(userProfile)
        }catch (e: Exception)
        {
            Resource.Failure(e)
        }
    }
    override suspend fun getUserProfile(userId: String): UserProfile? {

        var userProfile: UserProfile? = null;
        try {
            val result = firebaseFirestore.collection("users").document(userId).get().await()
            if(result != null)
            {
                userProfile = result.toObject<UserProfile>()!!
            }else
            {
                userProfile = null
            }
        }catch (e: Exception)
        {
            e.printStackTrace()
        }

        return userProfile
    }
}