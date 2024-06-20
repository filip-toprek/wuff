package com.filiptoprek.wuff.domain.usecase.reload;

import java.util.Calendar

class ValidateCardDateUseCase {
    operator fun invoke(ccYear: String, ccMonth: String): Boolean{

        val currentDate = Calendar.getInstance()
        val currentYear = currentDate.get(Calendar.YEAR)
        val currentMonth = currentDate.get(Calendar.MONTH) + 1 // Calendar.MONTH is zero-based

        if(ccYear.isEmpty() || ccMonth.isEmpty())
            return false

        val fullCcYear = if (ccYear.toInt() in 0..99) {
            val century = currentYear / 100 * 100
            century + ccYear.toInt()
        } else {
            return false
        }

        return when {
            ccYear.isEmpty() || ccMonth.isEmpty() -> false
            fullCcYear < currentYear -> false
            fullCcYear == currentYear && ccMonth.toInt() < currentMonth -> false
            ccMonth.toInt() !in 1..12 -> false
            else -> true
        }
    }
}
