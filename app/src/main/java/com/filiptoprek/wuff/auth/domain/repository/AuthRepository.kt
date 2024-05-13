package com.filiptoprek.wuff.auth.domain.repository

import android.content.Intent
import android.content.IntentSender
import androidx.lifecycle.MutableLiveData
import com.filiptoprek.wuff.auth.domain.model.Resource
import com.filiptoprek.wuff.auth.presentation.LoginResult
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    val currentUser: FirebaseUser?
    suspend fun login(email: String, password: String): Resource<FirebaseUser>
    suspend fun register(name: String, email: String, password: String): Resource<FirebaseUser>
    fun logout()
    fun firebaseSignInWithGoogle(googleAuthCredential: AuthCredential): MutableLiveData<Resource<FirebaseUser>>
}