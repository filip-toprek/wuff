package com.filiptoprek.wuff.presentation.reload

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filiptoprek.wuff.domain.model.Reload
import com.filiptoprek.wuff.domain.model.auth.Resource
import com.filiptoprek.wuff.domain.model.profile.UserProfile
import com.filiptoprek.wuff.domain.repository.auth.AuthRepository
import com.filiptoprek.wuff.domain.repository.profile.ProfileRepository
import com.filiptoprek.wuff.domain.repository.reload.ReloadRepository
import com.filiptoprek.wuff.domain.usecase.profile.ValidateAboutUserUseCase
import com.filiptoprek.wuff.domain.usecase.reload.ValidateReloadFormUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReloadViewModel @Inject constructor(
    private val reloadRepository: ReloadRepository,
    private val authRepository: AuthRepository,
    private val reloadFormUseCase: ValidateReloadFormUseCase
): ViewModel(){

    private val _reloadFlow = MutableStateFlow<Resource<Unit>?>(null)
    val reloadFlow: StateFlow<Resource<Unit>?> = _reloadFlow

    fun reloadBalance(reload: Reload, ccYear: String, ccMonth: String, Cvv: String, ccNum: String)
    {
        when(reloadFormUseCase.validateForm(ccYear, ccMonth, Cvv, reload, ccNum))
        {
            // handle failure
            -1 -> _reloadFlow.value = Resource.Failure(Exception("CARD_EXPIRED"))
            -2 -> _reloadFlow.value = Resource.Failure(Exception("BAD_CVV"))
            -3 -> _reloadFlow.value = Resource.Failure(Exception("BAD_AMOUNT"))
            -4 -> _reloadFlow.value = Resource.Failure(Exception("BAD_CARD"))
            // handle success
            1 -> {
                reload.reloadUser = authRepository.currentUser?.uid.toString()
                viewModelScope.launch {
                    _reloadFlow.value = Resource.Loading
                    try {
                        _reloadFlow.value = reloadRepository.reloadBalance(reload)
                    }catch (e: Exception) {
                        _reloadFlow.value = Resource.Failure(e)
                    }
                }
            }
        }

    }
}