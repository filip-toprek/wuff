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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReloadViewModel @Inject constructor(
    private val reloadRepository: ReloadRepository,
    private val authRepository: AuthRepository
): ViewModel(){

    private val _reloadFlow = MutableStateFlow<Resource<Unit>?>(null)
    val reloadFlow: StateFlow<Resource<Unit>?> = _reloadFlow

    fun reloadBalance(reload: Reload)
    {
        reload.reloadUser = authRepository.currentUser?.uid.toString()
        viewModelScope.launch {
            _reloadFlow.value = Resource.Loading
            try {
                reloadRepository.reloadBalance(reload)
                _reloadFlow.value = Resource.Success(Unit)
            }catch (e: Exception) {
                _reloadFlow.value = Resource.Failure(e)
            }
        }
    }
}