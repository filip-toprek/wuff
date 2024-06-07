package com.filiptoprek.wuff.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filiptoprek.wuff.domain.model.auth.Resource
import com.filiptoprek.wuff.domain.model.profile.UserProfile
import com.filiptoprek.wuff.domain.repository.auth.AuthRepository
import com.filiptoprek.wuff.domain.repository.home.HomeRepository
import com.filiptoprek.wuff.domain.repository.profile.ProfileRepository
import com.filiptoprek.wuff.domain.usecase.profile.ValidateAboutUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val profileRepository: ProfileRepository,
    private val authRepository: AuthRepository
): ViewModel(){
    private val _homeFlow = MutableStateFlow<Resource<List<UserProfile?>>?>(null)
    val homeFlow: StateFlow<Resource<List<UserProfile?>>?> = _homeFlow

    private var _walkerList = MutableStateFlow<List<UserProfile?>>(emptyList())
    val walkerList: StateFlow<List<UserProfile?>> = _walkerList

    init {
        if (_walkerList.value == emptyList<UserProfile?>()) {
            getWalkers()
        }
    }

    fun refresh(){
        _homeFlow.value = null
        getWalkers()
    }

    private fun getWalkers() {
        viewModelScope.launch {
            val currentUserProfile = profileRepository.getUserProfile(authRepository.currentUser?.uid.toString())
            _homeFlow.value = Resource.Loading
            val result = homeRepository.getWalkerList(currentUserProfile)

            if(result.isEmpty())
            {
                _homeFlow.value = Resource.Failure(Exception("Error home"))
            }else
            {
                _walkerList.value = result
                _homeFlow.value = Resource.Success(result)
            }
        }
    }

}