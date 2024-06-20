package com.filiptoprek.wuff.data.repository.withdraw

import android.util.Log
import androidx.compose.animation.core.snap
import arrow.fx.coroutines.Use
import com.filiptoprek.wuff.data.utils.await
import com.filiptoprek.wuff.domain.model.profile.UserProfile
import com.filiptoprek.wuff.domain.model.withdraw.Withdraw
import com.filiptoprek.wuff.domain.model.withdraw.WithdrawProfile
import com.filiptoprek.wuff.domain.model.withdraw.Withdrawals
import com.filiptoprek.wuff.domain.repository.withdraw.WithdrawRepository
import com.google.firebase.firestore.FirebaseFirestore
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import javax.inject.Inject

class WithdrawRepositoryImpl @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore
) : WithdrawRepository {

    // check if withdraw amount is lower than user's balance
    override suspend fun checkBalance(amount: Double, userId: String): Boolean {
        val user = firebaseFirestore.collection("users").document(userId)
            .get()
            .await()
            .toObject(UserProfile::class.java)

        if(amount > user?.balance!!)
            return false
        return true
    }

    override suspend fun createWithdrawalRequest(withdraw: Withdraw, withdrawProfile: WithdrawProfile, userProfile: UserProfile) {
        try {
            // Retrieve the user's profile document from Firestore
            val user = firebaseFirestore.collection("users")
                .document(userProfile.user.uid)
                .get()
                .await()
                .toObject(UserProfile::class.java)

            // Retrieve the withdrawals document for the user
            val withdrawals = firebaseFirestore.collection("withdrawals")
                .document(userProfile.user.uid)
                .get()
                .await()
                .toObject(Withdrawals::class.java)

            if (withdrawals == null) {
                // Create a new withdrawals document if it doesn't exist
                firebaseFirestore.collection("withdrawals").document(userProfile.user.uid).set(
                    Withdrawals(
                        withdrawals = listOf(withdraw),
                        withdrawProfile = withdrawProfile
                    )
                ).await()
            } else {
                // Update the existing withdrawals document
                firebaseFirestore.collection("withdrawals").document(userProfile.user.uid).update(
                    "withdrawals", withdrawals.withdrawals.plus(withdraw)
                ).await()
            }

            // Update the user's balance
            firebaseFirestore.collection("users").document(userProfile.user.uid).update(
                "balance", BigDecimal(user?.balance!! - withdraw.amount)
                    .setScale(2, RoundingMode.HALF_EVEN).toDouble()
            ).await()
        } catch (e: Exception) {
            Log.w("ERROR", "Failed to create withdrawal request: ${e.message}")
        }
    }

    override suspend fun getWithdrawals(userProfile: UserProfile): Withdrawals {
        return try {
            // Retrieve the withdrawals document for the user
            firebaseFirestore.collection("withdrawals").document(userProfile.user.uid)
                .get()
                .await()
                .toObject(Withdrawals::class.java) ?: Withdrawals()
        } catch (e: Exception) {
            Log.w("ERROR", "Failed to get withdrawals: ${e.message}")
            Withdrawals()
        }
    }
}