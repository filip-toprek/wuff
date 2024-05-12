package com.filiptoprek.wuff.di

import com.filiptoprek.wuff.auth.data.repository.AuthRepositoryImpl
import com.filiptoprek.wuff.auth.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Provides
    fun ProvideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    fun ProvideAuthRepository(impl: AuthRepositoryImpl): AuthRepository = impl
}