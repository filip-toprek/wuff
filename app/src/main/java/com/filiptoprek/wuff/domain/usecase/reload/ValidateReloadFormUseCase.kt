package com.filiptoprek.wuff.domain.usecase.reload;

import com.filiptoprek.wuff.domain.model.Reload

class ValidateReloadFormUseCase(
        private val validDate: ValidateCardDateUseCase,
        private val validateCVV: ValidateCVVUseCase,
) {
    fun validateForm(cardYear: String, cardMonth: String, Cvv: String, reload: Reload, ccNum: String): Int {
        val isDateValid = validDate(cardYear, cardMonth)
        val isCvvValid = validateCVV(Cvv)

        if(!isDateValid)
            return -1

        if(!isCvvValid)
            return -2

        if(reload.reloadAmount.isNaN() || reload.reloadAmount.isInfinite())
            return -3

        if(ccNum.isNullOrEmpty() ||ccNum.length != 16 )
            return -4

        return 1
    }
}