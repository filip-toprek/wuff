package com.filiptoprek.wuff.service

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MessagingService @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        saveTokenToFirestore(token)
    }

    private fun saveTokenToFirestore(token: String) {
        val userId = firebaseAuth.currentUser?.uid ?: return
        val userRef = firestore.collection("users").document(userId)
        userRef.update("fcmToken", token)
            .addOnSuccessListener {
                Log.d("FCMToken", "Token successfully updated!")
            }
            .addOnFailureListener { e ->
                Log.w("FCMToken", "Error updating token", e)
            }
    }
}
