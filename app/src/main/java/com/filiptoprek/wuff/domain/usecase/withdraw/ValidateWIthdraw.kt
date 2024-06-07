package com.filiptoprek.wuff.domain.usecase.withdraw

import com.filiptoprek.wuff.domain.model.withdraw.Withdraw

class ValidateWIthdraw {
    operator fun invoke(withdraw: Withdraw): Boolean{
        return !withdraw.completed && !withdraw.amount.isNaN()
    }
}