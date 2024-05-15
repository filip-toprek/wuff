package com.filiptoprek.wuff.domain.repository.profile

import androidx.lifecycle.LiveData
import com.filiptoprek.wuff.domain.model.auth.Resource
import com.filiptoprek.wuff.domain.model.profile.UserProfile

interface ProfileRepository {
    suspend fun getUserProfile(userId: String): UserProfile?
    suspend fun updateUserProfile(userProfile: UserProfile, userId: String): Resource<UserProfile>?
}