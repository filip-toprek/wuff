package com.filiptoprek.wuff.presentation.profile;

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filiptoprek.wuff.domain.model.auth.Resource
import com.filiptoprek.wuff.domain.model.profile.UserProfile
import com.filiptoprek.wuff.domain.repository.auth.AuthRepository;
import com.filiptoprek.wuff.domain.repository.profile.ProfileRepository;
import com.filiptoprek.wuff.domain.usecase.profile.ValidateAboutUserUseCase

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository,
    private val validateAboutUserUseCase: ValidateAboutUserUseCase,
    ): ViewModel(){

    private val _profileFlow = MutableStateFlow<Resource<UserProfile>?>(null)
    val profileFlow: StateFlow<Resource<UserProfile>?> = _profileFlow
    private var profile: UserProfile? = null
    init {
        if (profile == null) {
            observeCurrentUser()
        }
    }

    val userProfile: UserProfile?
        get() {
            return profile
        }

    private fun observeCurrentUser() {
        authRepository.currentUserLiveData.observeForever { currentUser ->
            if (currentUser != null) {
                viewModelScope.launch {
                    loadUserProfile()
                }
            }
        }
    }

    override fun onCleared() {
        authRepository.currentUserLiveData.removeObserver {  }
        super.onCleared()
    }

    fun refreshUser()
    {
        loadUserProfile()
    }

    fun becomeWalker(userProfile: UserProfile) {
        viewModelScope.launch {
            _profileFlow.value = Resource.Loading
            val result = profileRepository.becomeWalker(
                userProfile,
                authRepository.currentUser?.uid.toString()
            )
            if(result == null)
            {
                _profileFlow.value = Resource.Failure(Exception("Error profile"))
            }else
            {
                _profileFlow.value = result
                loadUserProfile()
            }
        }
    }

    fun updateUserProfile(userProfile: UserProfile): Boolean
    {
        if(!validateAboutUserUseCase.validateAboutUser(userProfile.aboutUser))
        {
            return false
        }

        viewModelScope.launch {
            _profileFlow.value = Resource.Loading
            val result = profileRepository.updateUserProfile(
                userProfile,
                authRepository.currentUser?.uid.toString()
            )
            if(result == null)
            {
                _profileFlow.value = Resource.Failure(Exception("Error profile 2"))
            }else
            {
                _profileFlow.value = result
                loadUserProfile()
            }
        }

        return true
    }
    private fun loadUserProfile() {
        _profileFlow.value = Resource.Loading
        viewModelScope.launch {
            val result = profileRepository.getUserProfile(authRepository.currentUser?.uid.toString())
            if(result == null)
            {
                _profileFlow.value = Resource.Failure(Exception("Error profile 3"))
            }else
            {
                profile = result
                _profileFlow.value = Resource.Success(result)
            }
        }
    }

}
