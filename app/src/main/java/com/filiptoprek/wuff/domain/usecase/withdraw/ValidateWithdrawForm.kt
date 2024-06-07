package com.filiptoprek.wuff.domain.usecase.withdraw

import com.filiptoprek.wuff.domain.model.reload.Reload
import com.filiptoprek.wuff.domain.model.withdraw.Withdraw
import com.filiptoprek.wuff.domain.model.withdraw.WithdrawProfile
import com.filiptoprek.wuff.domain.usecase.reload.ValidateCVVUseCase
import com.filiptoprek.wuff.domain.usecase.reload.ValidateCardDateUseCase

class ValidateWithdrawForm(
    private val validateWithdraw: ValidateWIthdraw,
    private val validateWithdrawProfile: ValidateWithdrawProfile,
) {
    fun validateForm(withdraw: Withdraw, withdrawProfile: WithdrawProfile): Int {
        val isWithdrawValid = validateWithdraw(withdraw)
        val isWithdrawProfileValid = validateWithdrawProfile(withdrawProfile)

        if(!isWithdrawValid)
            return -1

        if(!isWithdrawProfileValid)
            return -2

        return 1
    }
}