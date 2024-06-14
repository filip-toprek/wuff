package com.filiptoprek.wuff.presentation.reservation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import arrow.core.raise.result
import com.filiptoprek.wuff.domain.model.auth.Resource
import com.filiptoprek.wuff.domain.model.location.Location
import com.filiptoprek.wuff.domain.model.profile.UserProfile
import com.filiptoprek.wuff.domain.model.profile.Walker
import com.filiptoprek.wuff.domain.model.reservation.Reservation
import com.filiptoprek.wuff.domain.model.reservation.WalkType
import com.filiptoprek.wuff.domain.repository.auth.AuthRepository
import com.filiptoprek.wuff.domain.repository.home.HomeRepository
import com.filiptoprek.wuff.domain.repository.location.LocationRepository
import com.filiptoprek.wuff.domain.repository.profile.ProfileRepository
import com.filiptoprek.wuff.domain.repository.reservation.ReservationRepository
import com.filiptoprek.wuff.domain.usecase.reservation.ValidateReservationUseCase
import com.filiptoprek.wuff.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.internal.wait
import javax.inject.Inject

@HiltViewModel
class ReservationViewModel @Inject constructor(
    private val reservationRepository: ReservationRepository,
    private val authRepository: AuthRepository,
    private val homeRepository: HomeRepository,
    private val validateReservationUseCase: ValidateReservationUseCase,
    private val profileRepository: ProfileRepository,
    private val locationRepository: LocationRepository
): ViewModel(){

    private val _reservationFlow = MutableStateFlow<Resource<Any>?>(null)
    val reservationFlow: StateFlow<Resource<Any>?> = _reservationFlow

    private val _walkFlow = MutableStateFlow<Resource<Any>?>(null)
    val walkFlow: StateFlow<Resource<Any>?> = _walkFlow

    private val _reservationCreateFlow = MutableStateFlow<Resource<Any>?>(null)
    val reservationCreateFlow: StateFlow<Resource<Any>?> = _reservationCreateFlow

    private val _walkTypeList = MutableStateFlow<List<WalkType?>>(emptyList())
    val walkTypeList: StateFlow<List<WalkType?>> = _walkTypeList

    private val _reservationsList = MutableStateFlow<List<Reservation?>>(emptyList())
    val reservationsList: StateFlow<List<Reservation?>> = _reservationsList


    init {
        if (_walkTypeList.value == emptyList<UserProfile?>()) {
            getWalkTypeList()
        }
        if (_reservationsList.value == emptyList<Reservation?>()) {
            observeCurrentUser()
        }
    }

    private fun observeCurrentUser() {
        authRepository.currentUserLiveData.observeForever { currentUser ->
            if (currentUser != null) {
                viewModelScope.launch {
                    getReservationsList()
                }
            }
        }
    }

    override fun onCleared() {
        authRepository.currentUserLiveData.removeObserver {  }
        super.onCleared()
    }

    private suspend fun delayBeforeReset() {
        delay(1500)
        _reservationCreateFlow.value = null
    }

    fun createReservation(reservation: Reservation) {
        if(validateReservationUseCase.validateReservationUseCase(reservation))
        {
            viewModelScope.launch {
                _reservationCreateFlow.value = Resource.Loading
                reservation.userId = authRepository.currentUser?.uid.toString()
                val result = reservationRepository.createReservation(reservation)

                getReservationsList()
                _reservationCreateFlow.value = result
                delayBeforeReset()
            }
        }else
        {
            _reservationCreateFlow.value = Resource.Failure(Exception("Molimo unesite sva polja"))
        }
    }

    fun refreshReservations()
    {
        _reservationsList.value = emptyList<Reservation>()
        getReservationsList()
    }

    private fun getReservationsList() {
        viewModelScope.launch {
            _reservationFlow.value = Resource.Loading
            var result = if(profileRepository.getUserProfile(authRepository.currentUser?.uid.toString())?.walker?.approved == true)
            {
                reservationRepository.getReservationsForWalker(authRepository.currentUser?.uid.toString())
            }else{
                reservationRepository.getReservations(authRepository.currentUser?.uid.toString())
            }

            if(result == emptyList<Reservation>())
            {
                _reservationFlow.value = Resource.Failure(Exception("Error reservation list"))
            }else
            {
                result = if(profileRepository.getUserProfile(authRepository.currentUser?.uid.toString())?.walker?.approved == true) {
                    getUser(result)
                }else{
                    getWalker(result)
                }
                _reservationsList.value = result
                _reservationFlow.value = Resource.Success(result)
            }
        }
    }

    fun deleteReservation(reservation: Reservation) {
        viewModelScope.launch {
            _reservationFlow.value = Resource.Loading
            val result = reservationRepository.deleteReservation(reservation)
            _reservationFlow.value = result
            _reservationsList.value = emptyList<Reservation>()
            getReservationsList()
        }
    }

    fun startWalk(reservation: Reservation) {
        viewModelScope.launch {
            _walkFlow.value = Resource.Loading
            val walkerLocation = locationRepository.getLocation(reservation.walkerUserId)
            val userLocation = locationRepository.getLocation(reservation.userId)
            val result =
                if(walkerLocation.isWithinProximity(walkerLocation, userLocation, 100.0))
                {
                    reservationRepository.startWalk(reservation.reservationId)
                }else
                {
                    Resource.Failure(Exception("Error"))
                }
            _walkFlow.value = result
            _reservationsList.value = emptyList<Reservation>()
            getReservationsList()
        }
    }

    fun endWalk(reservation: Reservation) {
        viewModelScope.launch {
            _reservationFlow.value = Resource.Loading
            val result = reservationRepository.endWalk(reservation)
            _reservationFlow.value = result
            _reservationsList.value = emptyList<Reservation>()
            getReservationsList()
        }
    }

    fun acceptReservation(reservation: Reservation) {
        viewModelScope.launch {
            _reservationFlow.value = Resource.Loading
            val result = reservationRepository.acceptReservation(reservation.reservationId)
            _reservationFlow.value = result
            _reservationsList.value = emptyList<Reservation>()
            getReservationsList()
        }
    }
    fun declineReservation(reservation: Reservation) {
        viewModelScope.launch {
            _reservationFlow.value = Resource.Loading
            val result = reservationRepository.declineReservation(reservation)
            _reservationFlow.value = result
            _reservationsList.value = emptyList<Reservation>()
            getReservationsList()
        }
    }

    private suspend fun getWalker(resList: List<Reservation>) : List<Reservation>
    {
        for (reservation in resList)
        {
            for(walker in homeRepository.getWalkerList()) {
                if(walker?.user?.uid == reservation.walkerUserId)
                {
                    reservation.walker = walker
                }
            }
        }
        return resList
    }

    private suspend fun getUser(resList: List<Reservation>) : List<Reservation>
    {
        for (reservation in resList)
        {
            reservation.user = profileRepository.getUserProfile(reservation.userId)
        }
        return resList
    }

    private fun getWalkTypeList() {
        viewModelScope.launch {
            _reservationFlow.value = Resource.Loading
            val result = reservationRepository.getWalkTypes()

            if(result == emptyList<WalkType>())
            {
                _reservationFlow.value = Resource.Failure(Exception("Error reservation walk types"))
            }else
            {
                _walkTypeList.value = result
                _reservationFlow.value = Resource.Success(result)
            }
        }
    }
}