package com.filiptoprek.wuff.presentation.withdraw

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filiptoprek.wuff.domain.model.auth.Resource
import com.filiptoprek.wuff.domain.model.profile.UserProfile
import com.filiptoprek.wuff.domain.model.withdraw.Withdraw
import com.filiptoprek.wuff.domain.model.withdraw.WithdrawProfile
import com.filiptoprek.wuff.domain.model.withdraw.Withdrawals
import com.filiptoprek.wuff.domain.repository.auth.AuthRepository
import com.filiptoprek.wuff.domain.repository.home.HomeRepository
import com.filiptoprek.wuff.domain.repository.profile.ProfileRepository
import com.filiptoprek.wuff.domain.repository.withdraw.WithdrawRepository
import com.filiptoprek.wuff.domain.usecase.withdraw.ValidateWithdrawForm
import com.filiptoprek.wuff.domain.usecase.withdraw.ValidateWithdrawProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WithdrawViewModel @Inject constructor(
    private val withdrawRepository: WithdrawRepository,
    private val profileRepository: ProfileRepository,
    private val authRepository: AuthRepository,
    private val validateWithdrawForm: ValidateWithdrawForm
): ViewModel() {

    private val _withdrawList = MutableStateFlow<Withdrawals?>(null)
    val withdrawList: StateFlow<Withdrawals?> = _withdrawList

    private val _withdrawFlow = MutableStateFlow<Resource<Any>?>(null)
    val withdrawFlow: StateFlow<Resource<Any>?> = _withdrawFlow

    private val _createWithdrawFlow = MutableStateFlow<Resource<Any>?>(null)
    val createWithdrawFlow: StateFlow<Resource<Any>?> = _createWithdrawFlow

    init {
        if (_withdrawList.value == null) {
            getWithdrawalsList()
        }
    }

    // get withdrawals list
    fun refresh(){
        _withdrawList.value = Withdrawals()
        getWithdrawalsList()
    }

    // helper function to delay the reset
    private suspend fun delayBeforeReset() {
        delay(1500)
        _createWithdrawFlow.value = null
    }


    // get withdrawals list from firestore
    private fun getWithdrawalsList() {
        viewModelScope.launch {
            val currentUserProfile = profileRepository.getUserProfile(authRepository.currentUser?.uid.toString())
            if(currentUserProfile?.walker?.approved != true)
                return@launch

            _withdrawFlow.value = Resource.Loading
            val result = withdrawRepository.getWithdrawals(currentUserProfile!!)
            _withdrawList.value = result
            _withdrawFlow.value = Resource.Success(result)
        }
    }


    // create a new withdrawal request
    fun createWithdrawal(withdraw: Withdraw, withdrawProfile: WithdrawProfile) {
        viewModelScope.launch {
            when (validateWithdrawForm.validateForm(
                withdraw,
                withdrawProfile,
                withdrawRepository,
                authRepository
            )) {
                // handle failure
                -1 -> _createWithdrawFlow.value = Resource.Failure(Exception("BAD_AMOUNT"))
                -2 -> _createWithdrawFlow.value = Resource.Failure(Exception("BAD_IBAN"))
                // handle success
                1 -> {
                    viewModelScope.launch {
                        val currentUserProfile =
                            profileRepository.getUserProfile(authRepository.currentUser?.uid.toString())
                        _createWithdrawFlow.value = Resource.Loading
                        val result = withdrawRepository.createWithdrawalRequest(
                            withdraw,
                            withdrawProfile,
                            currentUserProfile!!
                        )
                        _createWithdrawFlow.value = Resource.Success(result)
                        delayBeforeReset()
                    }
                }
            }
        }
    }
}