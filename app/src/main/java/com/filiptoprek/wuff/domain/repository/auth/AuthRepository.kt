package com.filiptoprek.wuff.domain.repository.auth

import androidx.lifecycle.LiveData
import com.filiptoprek.wuff.domain.model.auth.Resource
import com.filiptoprek.wuff.domain.model.profile.UserProfile
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    val currentUser: FirebaseUser?
    val currentUserLiveData : LiveData<FirebaseUser?>
    suspend fun login(email: String, password: String): Resource<FirebaseUser>
    suspend fun register(name: String, email: String, password: String): Resource<FirebaseUser>
    fun logout()
    suspend fun firebaseSignInWithGoogle(googleAuthCredential: AuthCredential): Resource<FirebaseUser>
    suspend fun createUserProfile(userId: String, userProfile: UserProfile)

}