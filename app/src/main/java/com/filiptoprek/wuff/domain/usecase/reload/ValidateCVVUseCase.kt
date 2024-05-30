package com.filiptoprek.wuff.domain.usecase.reload;

class ValidateCVVUseCase {
    operator fun invoke(cvv: String): Boolean{
        return cvv.length == 3 && cvv.isNotEmpty()
    }
}
