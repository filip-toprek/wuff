package com.filiptoprek.wuff.data.repository.reload

import com.filiptoprek.wuff.data.utils.await
import com.filiptoprek.wuff.domain.model.reload.Reload
import com.filiptoprek.wuff.domain.model.auth.Resource
import com.filiptoprek.wuff.domain.model.profile.Transaction
import com.filiptoprek.wuff.domain.model.profile.UserProfile
import com.filiptoprek.wuff.domain.repository.reload.ReloadRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class ReloadRepositoryImpl @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore
) :ReloadRepository {
    override suspend fun reloadBalance(reload: Reload): Resource<Unit> {
        return try {
            // Retrieve the user's profile document from Firestore
            val userProfile = firebaseFirestore.collection("users").document(reload.reloadUser)
                .get()
                .await()
                .toObject(UserProfile::class.java)!!

            // Update the user's balance
            firebaseFirestore.collection("users").document(reload.reloadUser)
                .update("balance", userProfile.balance.plus(reload.reloadAmount))
                .await()

            // Add the transaction to the user's transaction history
            addTransaction(reload)
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }

    private suspend fun addTransaction(reload: Reload): Resource<Unit> {
        return try {
            // Retrieve the current list of transactions for the user
            val userProfile = firebaseFirestore.collection("users").document(reload.reloadUser)
                .get()
                .await()
                .toObject(UserProfile::class.java)

            val transactionList = userProfile?.transactions ?: emptyList()

            // Create a new transaction
            val newTransaction = Transaction(
                UUID.randomUUID().toString(),
                reload.reloadDate,
                reload.reloadAmount,
                true
            )

            // Update the user's transactions with the new transaction
            firebaseFirestore.collection("users").document(reload.reloadUser)
                .update("transactions", transactionList.plus(newTransaction))
                .await()

            Resource.Success(Unit)
        } catch (e: Exception) {
            // Handle the failure case by logging the transaction with a failure status
            val userProfile = firebaseFirestore.collection("users").document(reload.reloadUser)
                .get()
                .await()
                .toObject(UserProfile::class.java)

            val transactionList = userProfile?.transactions ?: emptyList()

            val failedTransaction = Transaction(
                UUID.randomUUID().toString(),
                reload.reloadDate,
                reload.reloadAmount,
                false
            )

            firebaseFirestore.collection("users").document(reload.reloadUser)
                .update("transactions", transactionList.plus(failedTransaction))
                .await()

            Resource.Failure(e)
        }
    }
}