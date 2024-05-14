package com.filiptoprek.wuff.domain.usecase

import android.util.Patterns

class ValidateEmailUseCase {
    operator fun invoke(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}