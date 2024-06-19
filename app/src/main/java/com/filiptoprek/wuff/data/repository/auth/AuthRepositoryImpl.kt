package com.filiptoprek.wuff.data.repository.auth

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.filiptoprek.wuff.data.utils.await
import com.filiptoprek.wuff.domain.model.auth.Resource
import com.filiptoprek.wuff.domain.model.profile.UserData
import com.filiptoprek.wuff.domain.model.profile.UserProfile
import com.filiptoprek.wuff.domain.repository.auth.AuthRepository
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore,
) : AuthRepository {
    override val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    private val _currentUserLiveData: MutableLiveData<FirebaseUser?> = MutableLiveData()
    override val currentUserLiveData: LiveData<FirebaseUser?>
        get() = _currentUserLiveData

    init {
        firebaseAuth.addAuthStateListener { firebaseAuth ->
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
            createUserProfile(
                result.user?.uid.toString(),
                UserProfile(
                    balance = 0.0,
                    aboutUser = "Jako volim ljude i šetnje uz plažu",
                    numOfWalks = 0,
                    user = UserData(
                        uid = result.user?.uid.toString(),
                        name =  result.user?.displayName.toString(),
                        profilePhotoUrl =  result.user?.photoUrl.toString())
                ))
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
    override suspend fun firebaseSignInWithGoogle(googleAuthCredential: AuthCredential): Resource<FirebaseUser> {

        return try {
            val result = firebaseAuth.signInWithCredential(googleAuthCredential).await()
            if(result?.additionalUserInfo?.isNewUser == true)
            {
               createUserProfile(
                   result.user?.uid.toString(),
                   UserProfile(
                       balance = 0.0,
                       aboutUser = "Jako volim ljude i šetnje uz plažu",
                       numOfWalks = 0,
                       user = UserData(
                           result.user?.uid.toString(),
                           result.user?.displayName.toString(),
                           result.user?.photoUrl.toString())
                   ))
            }
            Resource.Success(result?.user!!)
        }catch (e: Exception)
        {
            Resource.Failure(e)
        }
    }

    override suspend fun createUserProfile(userId: String, userProfile: UserProfile) {
        firebaseFirestore.collection("users").document(userId).set(userProfile).await()
        firebaseFirestore.collection("locations").document(userId).set(LatLng(0.0,0.0)).await()
    }

}