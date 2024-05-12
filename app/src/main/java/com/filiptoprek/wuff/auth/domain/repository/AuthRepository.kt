package com.filiptoprek.wuff.auth.domain.repository

import com.filiptoprek.wuff.auth.domain.model.Resource
import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    val currentUser: FirebaseUser?
    suspend fun Login(email: String, password: String): Resource<FirebaseUser>
    suspend fun Register(name: String, email: String, password: String): Resource<FirebaseUser>
    fun Logout()
}