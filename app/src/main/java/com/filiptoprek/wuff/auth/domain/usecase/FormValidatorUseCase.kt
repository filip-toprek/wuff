package com.filiptoprek.wuff.auth.domain.usecase

class FormValidatorUseCase(
    private val validName: ValidateNameUseCase,
    private val validEmail: ValidateEmailUseCase,
    private val validPassword: ValidatePasswordUseCase
) {
    fun validateForm(name: String = "john doe", email: String, password: String, verifyPassword: String = password): Int {
        val isNameValid = validName(name)
        val isEmailValid = validEmail(email)
        val isPasswordValid = validPassword(password)

        if(!isNameValid)
            return -1
        if(!isEmailValid)
            return -2
        if(!isPasswordValid)
            return -3
        if(password != verifyPassword)
            return -4

        return 1
    }
}