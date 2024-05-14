package com.filiptoprek.wuff.domain.usecase

class ValidatePasswordUseCase {
    operator fun invoke(password: String): Boolean{
        return password.length >= 8 && password.any { it.isLowerCase() } &&
                password.any { it.isDigit() }
    }
}