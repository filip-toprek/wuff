package com.filiptoprek.wuff.domain.usecase.withdraw

import com.filiptoprek.wuff.domain.model.withdraw.Withdraw
import com.filiptoprek.wuff.domain.repository.auth.AuthRepository
import com.filiptoprek.wuff.domain.repository.withdraw.WithdrawRepository

class ValidateWIthdraw {
    suspend operator fun invoke(withdraw: Withdraw, withdrawRepository: WithdrawRepository, authRepository: AuthRepository): Boolean{
        return !withdraw.completed && !withdraw.amount.isNaN() && withdrawRepository.checkBalance(withdraw.amount, authRepository.currentUser?.uid.toString())
    }
}