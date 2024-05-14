package com.filiptoprek.wuff.di

import android.app.Application
import android.content.Context
import com.filiptoprek.wuff.R
import com.filiptoprek.wuff.data.repository.AuthRepositoryImpl
import com.filiptoprek.wuff.domain.repository.AuthRepository
import com.filiptoprek.wuff.domain.usecase.FormValidatorUseCase
import com.filiptoprek.wuff.domain.usecase.ValidateEmailUseCase
import com.filiptoprek.wuff.domain.usecase.ValidateNameUseCase
import com.filiptoprek.wuff.domain.usecase.ValidatePasswordUseCase
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Singleton
    @Provides
    fun provideAuthRepository(impl: AuthRepositoryImpl): AuthRepository = impl

    @Provides
    @Singleton
    fun provideContext(application: Application): Context = application.applicationContext

    @Provides
    @Singleton
    fun provideGoogleSignInClient(context: Context): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.web_client_id))
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(context, gso)
    }

    @Provides
    @Singleton
    fun provideFormValidatorUseCase(): FormValidatorUseCase {
        return FormValidatorUseCase(
            validName = ValidateNameUseCase(),
            validEmail = ValidateEmailUseCase(),
            validPassword = ValidatePasswordUseCase(),
        )
    }

}