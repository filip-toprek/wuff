package com.filiptoprek.wuff.presentation.location

import android.content.Context
import android.location.Geocoder
import android.util.Log
import androidx.core.app.ServiceCompat.startForeground
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filiptoprek.wuff.domain.model.auth.Resource
import com.filiptoprek.wuff.domain.model.location.LatLngInterpolator
import com.filiptoprek.wuff.domain.model.location.Location
import com.filiptoprek.wuff.domain.model.location.LocationPoint
import com.filiptoprek.wuff.domain.model.profile.UserProfile
import com.filiptoprek.wuff.domain.repository.auth.AuthRepository
import com.filiptoprek.wuff.domain.repository.home.HomeRepository
import com.filiptoprek.wuff.domain.repository.location.LocationClient
import com.filiptoprek.wuff.domain.repository.location.LocationRepository
import com.filiptoprek.wuff.domain.repository.profile.ProfileRepository
import com.filiptoprek.wuff.domain.repository.reservation.ReservationRepository
import com.filiptoprek.wuff.domain.usecase.reservation.ValidateReservationUseCase
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val profileRepository: ProfileRepository,
    private val authRepository: AuthRepository,
    private val reservationRepository: ReservationRepository,
    private val locationClient: LocationClient,
    private val context: Context
): ViewModel(){

    private val _locationFlow = MutableStateFlow<Resource<Any>?>(null)
    val locationFlow: StateFlow<Resource<Any>?> = _locationFlow

    private val _location = MutableLiveData<Location>()
    val location: LiveData<Location> get() = _location

    private val _locationPoints = MutableLiveData<List<Location>>()
    val locationPoints: LiveData<List<Location>> = _locationPoints

    // Fetch location points from repository
    fun fetchLocationPoints(walkerId: String, reservationId: String) {
        viewModelScope.launch {
            try {
                val points = locationRepository.getLocationPoints(walkerId, reservationId)
                _locationPoints.value = points
            } catch (e: Exception) {
                // Handle error
                Log.e("MapViewModel", "Error fetching location points: ${e.message}")
            }
        }
    }

    fun clearLocationPoints()
    {
        _locationPoints.value = emptyList()
    }

    private fun distanceBetweenPoints(start: LatLng, end: LatLng): Double {
        val lat1 = start.latitude
        val lon1 = start.longitude
        val lat2 = end.latitude
        val lon2 = end.longitude

        val R = 6371 // Radius of the Earth in kilometers
        val latDistance = Math.toRadians(lat2 - lat1)
        val lonDistance = Math.toRadians(lon2 - lon1)
        val a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        val distance = R * c

        return distance
    }

    // Function to calculate total distance of a polyline
    fun calculatePolylineDistance(polylinePoints: List<LatLng>, reservationId: String) {
        var totalDistance = 0.0

        // Iterate through each point in the polyline
        for (i in 0 until polylinePoints.size - 1) {
            val start = polylinePoints[i]
            val end = polylinePoints[i + 1]
            totalDistance += distanceBetweenPoints(start, end)
        }

        viewModelScope.launch {
            reservationRepository.updateWalkDistance(reservationId, BigDecimal(totalDistance).setScale(2, RoundingMode.HALF_EVEN).toDouble())
        }
    }

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
            try {
                val location = locationRepository.getLocation(authRepository.currentUser?.uid.toString())
                profileRepository.updateUserCity(authRepository.currentUser?.uid.toString(), getCityName(context, location.latitude, location.longitude)!!)
            }catch (e: Exception)
            {
                e.printStackTrace()
            }
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