package com.filiptoprek.wuff.domain.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.filiptoprek.wuff.domain.model.auth.Resource
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    val currentUser: FirebaseUser?

    val currentUserLiveData : LiveData<FirebaseUser?>
    suspend fun login(email: String, password: String): Resource<FirebaseUser>
    suspend fun register(name: String, email: String, password: String): Resource<FirebaseUser>
    fun logout()
    fun firebaseSignInWithGoogle(googleAuthCredential: AuthCredential): MutableLiveData<Resource<FirebaseUser>>
}