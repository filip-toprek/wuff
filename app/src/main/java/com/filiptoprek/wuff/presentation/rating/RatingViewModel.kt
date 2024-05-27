package com.filiptoprek.wuff.presentation.rating

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filiptoprek.wuff.domain.model.auth.Resource
import com.filiptoprek.wuff.domain.model.rating.Review
import com.filiptoprek.wuff.domain.repository.rating.RatingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RatingViewModel @Inject constructor(
    private val ratingRepository: RatingRepository,
): ViewModel() {
    private val _ratingFlow = MutableStateFlow<Resource<Any>?>(null)
    val ratingFlow: StateFlow<Resource<Any>?> = _ratingFlow

    fun addReview(review: Review)
    {
        viewModelScope.launch {
            _ratingFlow.value = Resource.Loading
            _ratingFlow.value = ratingRepository.rateUser(review)
        }
    }

}