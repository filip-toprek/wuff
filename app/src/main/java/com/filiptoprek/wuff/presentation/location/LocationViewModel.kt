package com.filiptoprek.wuff.presentation.location

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filiptoprek.wuff.domain.model.auth.Resource
import com.filiptoprek.wuff.domain.model.location.Location
import com.filiptoprek.wuff.domain.model.profile.UserProfile
import com.filiptoprek.wuff.domain.repository.auth.AuthRepository
import com.filiptoprek.wuff.domain.repository.home.HomeRepository
import com.filiptoprek.wuff.domain.repository.location.LocationRepository
import com.filiptoprek.wuff.domain.repository.profile.ProfileRepository
import com.filiptoprek.wuff.domain.repository.reservation.ReservationRepository
import com.filiptoprek.wuff.domain.usecase.reservation.ValidateReservationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val locationRepository: LocationRepository
): ViewModel(){

    private val _locationFlow = MutableStateFlow<Resource<Any>?>(null)
    val locationFlow: StateFlow<Resource<Any>?> = _locationFlow

    private val _location = MutableLiveData<Location>()
    val location: LiveData<Location> get() = _location

    fun getWalkerLocation(walker: UserProfile)
    {
        viewModelScope.launch {
            _locationFlow.value = Resource.Loading
            _location.value = locationRepository.getLocation(walker.user.uid)
            if(_location.value == Location())
            {
                _locationFlow.value = Resource.Failure(Exception("Location empty"))
            }else
            {
                _locationFlow.value = Resource.Success(Unit)
            }
        }
    }
}