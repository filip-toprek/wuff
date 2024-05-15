package com.filiptoprek.wuff.di

import android.app.Application
import android.content.Context
import com.filiptoprek.wuff.R
import com.filiptoprek.wuff.data.repository.auth.AuthRepositoryImpl
import com.filiptoprek.wuff.data.repository.profile.ProfileRepositoryImpl
import com.filiptoprek.wuff.domain.repository.auth.AuthRepository
import com.filiptoprek.wuff.domain.repository.profile.ProfileRepository
import com.filiptoprek.wuff.domain.usecase.auth.FormValidatorUseCase
import com.filiptoprek.wuff.domain.usecase.auth.ValidateEmailUseCase
import com.filiptoprek.wuff.domain.usecase.auth.ValidateNameUseCase
import com.filiptoprek.wuff.domain.usecase.auth.ValidatePasswordUseCase
import com.filiptoprek.wuff.domain.usecase.profile.ValidateAboutUserUseCase
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideAuthRepository(impl: AuthRepositoryImpl): AuthRepository = impl

    @Provides
    @Singleton
    fun provideProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository = impl

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

    @Provides
    @Singleton
    fun provideValidateAboutUserUseCase(): ValidateAboutUserUseCase {
        return ValidateAboutUserUseCase()
    }

}