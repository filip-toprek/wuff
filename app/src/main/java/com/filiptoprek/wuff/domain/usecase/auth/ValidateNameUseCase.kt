package com.filiptoprek.wuff.domain.usecase.auth

class ValidateNameUseCase {
    operator fun invoke(name: String): Boolean{
        return name.length in 4.. 50
    }
}