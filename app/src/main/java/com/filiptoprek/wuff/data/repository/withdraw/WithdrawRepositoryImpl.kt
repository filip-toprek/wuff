package com.filiptoprek.wuff.data.repository.withdraw

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
    override suspend fun createWithdrawalRequest(withdraw: Withdraw, withdrawProfile: WithdrawProfile, userProfile: UserProfile) {
        try {
            val user = firebaseFirestore.collection("users").document(userProfile.user.uid)
                .get()
                .await()
                .toObject(UserProfile::class.java)

            val withdrawals = firebaseFirestore.collection("withdrawals").document(userProfile.user.uid)
                .get()
                .await()
                .toObject(Withdrawals::class.java)

            if(withdrawals == null)
            {
                firebaseFirestore.collection("withdrawals").document(userProfile.user.uid).set(
                    Withdrawals(
                        withdrawals = listOf(
                            withdraw
                        ),
                        withdrawProfile = withdrawProfile
                    )
                )
            }else
            {
                firebaseFirestore.collection("withdrawals").document(userProfile.user.uid).update(
                    "withdrawals", withdrawals.withdrawals.plus(withdraw)
                )
            }

            firebaseFirestore.collection("users").document(userProfile.user.uid).update("balance", BigDecimal(user?.balance!! - withdraw.amount).setScale(2, RoundingMode.HALF_EVEN).toDouble())
        } catch (e: Exception) {
            //
        }
    }

    override suspend fun getWithdrawals(userProfile: UserProfile): Withdrawals {
        return try {
            firebaseFirestore.collection("withdrawals").document(userProfile.user.uid)
                .get()
                .await().toObject(Withdrawals::class.java)!!
        } catch (e: Exception) {
            Withdrawals()
        }
    }
}