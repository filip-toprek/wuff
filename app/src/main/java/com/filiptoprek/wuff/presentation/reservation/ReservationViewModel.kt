package com.filiptoprek.wuff.presentation.reservation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filiptoprek.wuff.domain.model.auth.Resource
import com.filiptoprek.wuff.domain.model.profile.UserProfile
import com.filiptoprek.wuff.domain.model.reservation.WalkType
import com.filiptoprek.wuff.domain.repository.home.HomeRepository
import com.filiptoprek.wuff.domain.repository.reservation.ReservationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReservationViewModel @Inject constructor(
    private val reservationRepository: ReservationRepository,
): ViewModel(){

    private val _reservationFlow = MutableStateFlow<Resource<List<WalkType?>>?>(null)
    val reservationFlow: StateFlow<Resource<List<WalkType?>>?> = _reservationFlow

    private val _walkTypeList = MutableStateFlow<List<WalkType?>>(emptyList())
    val walkTypeList: StateFlow<List<WalkType?>> = _walkTypeList

    init {
        if (_walkTypeList.value == emptyList<UserProfile?>()) {
            getWalkTypeList()
        }
    }

    private fun getWalkTypeList() {
        viewModelScope.launch {
            _reservationFlow.value = Resource.Loading
            val result = reservationRepository.getWalkTypes()

            if(result == null)
            {
                _reservationFlow.value = Resource.Failure(Exception("Error"))
            }else
            {
                _walkTypeList.value = result
                _reservationFlow.value = Resource.Success(result)
            }
        }
    }
}