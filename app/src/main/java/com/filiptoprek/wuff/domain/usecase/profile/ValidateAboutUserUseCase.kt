package com.filiptoprek.wuff.domain.usecase.profile

class ValidateAboutUserUseCase {
    fun validateAboutUser(aboutUser: String): Boolean {
        return aboutUser.length <= 50 || aboutUser.isEmpty()
    }
}