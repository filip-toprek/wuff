package com.filiptoprek.wuff.auth.data.repository

import com.filiptoprek.wuff.auth.data.utils.await
import com.filiptoprek.wuff.auth.domain.model.Resource
import com.filiptoprek.wuff.auth.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {
    override val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    override suspend fun Login(email: String, password: String): Resource<FirebaseUser> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Resource.Success(result.user!!)
        }catch (e: Exception)
        {
            e.printStackTrace()
            Resource.Faliure(e)
        }
    }

    override suspend fun Register(
        name: String,
        email: String,
        password: String
    ): Resource<FirebaseUser> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            result?.user?.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(name).build())?.await()
            Resource.Success(result.user!!)
        }catch (e: Exception)
        {
            e.printStackTrace()
            Resource.Faliure(e)
        }
    }

    override fun Logout() {
        firebaseAuth.signOut()
    }
}