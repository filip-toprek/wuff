package com.filiptoprek.wuff.domain.usecase.withdraw

import com.filiptoprek.wuff.domain.model.withdraw.Withdraw
import com.filiptoprek.wuff.domain.model.withdraw.WithdrawProfile

class ValidateWithdrawProfile {
    operator fun invoke(withdrawProfile: WithdrawProfile): Boolean{
        return withdrawProfile.iban.length in 1..21 && withdrawProfile.iban.startsWith("HR", ignoreCase = true)
    }
}