package com.filiptoprek.wuff.domain.repository.home

import com.filiptoprek.wuff.domain.model.auth.Resource
import com.filiptoprek.wuff.domain.model.profile.UserProfile

interface HomeRepository {
    suspend fun getWalkerList(): List<UserProfile?>
}