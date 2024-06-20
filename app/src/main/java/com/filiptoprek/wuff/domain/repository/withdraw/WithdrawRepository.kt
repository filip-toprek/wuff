package com.filiptoprek.wuff.domain.repository.withdraw

import com.filiptoprek.wuff.domain.model.profile.UserProfile
import com.filiptoprek.wuff.domain.model.withdraw.Withdraw
import com.filiptoprek.wuff.domain.model.withdraw.WithdrawProfile
import com.filiptoprek.wuff.domain.model.withdraw.Withdrawals

interface WithdrawRepository {
    suspend fun createWithdrawalRequest(withdraw: Withdraw, withdrawProfile: WithdrawProfile, userProfile: UserProfile)
    suspend fun getWithdrawals(userProfile: UserProfile): Withdrawals
    suspend fun checkBalance(amount: Double, userId: String): Boolean
}