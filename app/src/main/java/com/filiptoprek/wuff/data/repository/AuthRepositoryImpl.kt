package com.filiptoprek.wuff.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.filiptoprek.wuff.data.utils.await
import com.filiptoprek.wuff.domain.model.auth.Resource
import com.filiptoprek.wuff.domain.repository.AuthRepository
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val context: Context,
) : AuthRepository {
    override val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    private val _currentUserLiveData: MutableLiveData<FirebaseUser?> = MutableLiveData()
    override val currentUserLiveData: LiveData<FirebaseUser?>
        get() = _currentUserLiveData

    init {
        // Listen for changes in the authentication state
        firebaseAuth.addAuthStateListener { firebaseAuth ->
            // Update the value of currentUserLiveData when authentication state changes
            _currentUserLiveData.value = firebaseAuth.currentUser
        }
    }

    override suspend fun login(email: String, password: String): Resource<FirebaseUser> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Resource.Success(result.user!!)
        }catch (e: Exception)
        {
            e.printStackTrace()
            when(e.message)
            {
                "The supplied auth credential is incorrect, malformed or has expired." -> Resource.Failure(Exception("ERROR|Krivo uneseni podatci ili račun ne postoji"))
                "We have blocked all requests from this device due to unusual activity. Try again later. [ Access to this account has been temporarily disabled due to many failed login attempts. You can immediately restore it by resetting your password or you can try again later. ]"
                -> Resource.Failure(Exception("ERROR|Previše neuspjelih pokušaja za prijavu"))
                "The user account has been disabled by an administrator." -> Resource.Failure(Exception("ERROR|Administrator je onemogućio korisnički račun"))
                else -> Resource.Failure(Exception("ERROR|Došlo je do greške"))
            }
        }
    }

    override suspend fun register(
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
            when(e.message)
            {
                "We have blocked all requests from this device due to unusual activity. Try again later. [ Access to this account has been temporarily disabled due to many failed login attempts. You can immediately restore it by resetting your password or you can try again later. ]"
                -> Resource.Failure(Exception("ERROR|Previše neuspjelih pokušaja za prijavu"))
                else -> Resource.Failure(Exception("ERROR|Došlo je do greške"))
            }
        }
    }

    override fun logout() {
        firebaseAuth.signOut()
    }

    // Sign in using google
    override fun firebaseSignInWithGoogle(googleAuthCredential: AuthCredential): MutableLiveData<Resource<FirebaseUser>> {
        val authenticatedUserMutableLiveData: MutableLiveData<Resource<FirebaseUser>> =
            MutableLiveData()

        firebaseAuth.signInWithCredential(googleAuthCredential).addOnCompleteListener { authTask ->
            if (authTask.isSuccessful) {
                val firebaseUser: FirebaseUser? = firebaseAuth.currentUser
                if (firebaseUser != null) {
                    authenticatedUserMutableLiveData.value = Resource.Success(firebaseUser)
                }
            } else {

                authenticatedUserMutableLiveData.value = authTask.exception?.let {
                    Resource.Failure(it)
                }

            }


        }
        return authenticatedUserMutableLiveData
    }

}