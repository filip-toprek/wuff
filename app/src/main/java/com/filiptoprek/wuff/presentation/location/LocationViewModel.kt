package com.filiptoprek.wuff.presentation.location

import android.content.Context
import android.location.Geocoder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filiptoprek.wuff.domain.model.auth.Resource
import com.filiptoprek.wuff.domain.model.location.LatLngInterpolator
import com.filiptoprek.wuff.domain.model.location.Location
import com.filiptoprek.wuff.domain.model.profile.UserProfile
import com.filiptoprek.wuff.domain.repository.auth.AuthRepository
import com.filiptoprek.wuff.domain.repository.home.HomeRepository
import com.filiptoprek.wuff.domain.repository.location.LocationRepository
import com.filiptoprek.wuff.domain.repository.profile.ProfileRepository
import com.filiptoprek.wuff.domain.repository.reservation.ReservationRepository
import com.filiptoprek.wuff.domain.usecase.reservation.ValidateReservationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val profileRepository: ProfileRepository,
    private val authRepository: AuthRepository,
    private val context: Context
): ViewModel(){

    private val _locationFlow = MutableStateFlow<Resource<Any>?>(null)
    val locationFlow: StateFlow<Resource<Any>?> = _locationFlow

    private val _location = MutableLiveData<Location>()
    val location: LiveData<Location> get() = _location

    fun startLocationUpdates(walker: UserProfile) {
        viewModelScope.launch {
            while (isActive) {
                _locationFlow.value = Resource.Loading
                val newLocation = locationRepository.getLocation(walker.user.uid)
                if (newLocation == Location()) {
                    _locationFlow.value = Resource.Failure(Exception("Location empty"))
                } else {
                    _location.postValue(newLocation)
                    _locationFlow.value = Resource.Success(Unit)
                }
                delay(5000)
            }
        }
    }

    fun getLocationOnStart() {
        viewModelScope.launch {
            val location = locationRepository.getLocation(authRepository.currentUser?.uid.toString())
            profileRepository.updateUserCity(authRepository.currentUser?.uid.toString(), getCityName(context, location.latitude, location.longitude)!!)
        }
    }

    suspend private fun getCityName(context: android.content.Context, latitude: Double, longitude: Double): String? {
        return withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                if (addresses?.isNotEmpty()!!) {
                    addresses[0].locality
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    fun stopLocationUpdates() {
        viewModelScope.coroutineContext.cancelChildren()
    }
}