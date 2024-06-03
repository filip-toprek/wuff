package com.filiptoprek.wuff.di

import android.app.Application
import android.content.Context
import com.filiptoprek.wuff.R
import com.filiptoprek.wuff.data.repository.auth.AuthRepositoryImpl
import com.filiptoprek.wuff.data.repository.home.HomeRepositoryImpl
import com.filiptoprek.wuff.data.repository.location.LocationClientImpl
import com.filiptoprek.wuff.data.repository.location.LocationRepositoryImpl
import com.filiptoprek.wuff.data.repository.profile.ProfileRepositoryImpl
import com.filiptoprek.wuff.data.repository.rating.RatingRepositoryImpl
import com.filiptoprek.wuff.data.repository.reload.ReloadRepositoryImpl
import com.filiptoprek.wuff.data.repository.reservation.ReservationRepositoryImpl
import com.filiptoprek.wuff.domain.repository.auth.AuthRepository
import com.filiptoprek.wuff.domain.repository.home.HomeRepository
import com.filiptoprek.wuff.domain.repository.location.LocationClient
import com.filiptoprek.wuff.domain.repository.location.LocationRepository
import com.filiptoprek.wuff.domain.repository.profile.ProfileRepository
import com.filiptoprek.wuff.domain.repository.rating.RatingRepository
import com.filiptoprek.wuff.domain.repository.reload.ReloadRepository
import com.filiptoprek.wuff.domain.repository.reservation.ReservationRepository
import com.filiptoprek.wuff.domain.usecase.auth.FormValidatorUseCase
import com.filiptoprek.wuff.domain.usecase.auth.ValidateEmailUseCase
import com.filiptoprek.wuff.domain.usecase.auth.ValidateNameUseCase
import com.filiptoprek.wuff.domain.usecase.auth.ValidatePasswordUseCase
import com.filiptoprek.wuff.domain.usecase.profile.ValidateAboutUserUseCase
import com.filiptoprek.wuff.domain.usecase.reload.ValidateCVVUseCase
import com.filiptoprek.wuff.domain.usecase.reload.ValidateCardDateUseCase
import com.filiptoprek.wuff.domain.usecase.reload.ValidateReloadFormUseCase
import com.filiptoprek.wuff.domain.usecase.reservation.ValidateReservationUseCase
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
    fun provideReservationRepository(impl: ReservationRepositoryImpl): ReservationRepository = impl

    @Provides
    @Singleton
    fun provideProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository = impl

    @Provides
    @Singleton
    fun provideHomeRepository(impl: HomeRepositoryImpl): HomeRepository = impl

    @Provides
    @Singleton
    fun provideReloadRepository(impl: ReloadRepositoryImpl): ReloadRepository = impl

    @Provides
    @Singleton
    fun provideRatingRepository(impl: RatingRepositoryImpl): RatingRepository = impl

    @Provides
    @Singleton
    fun provideLocationClient(impl: LocationClientImpl): LocationClient = impl

    @Provides
    @Singleton
    fun provideLocationRepository(impl: LocationRepositoryImpl): LocationRepository = impl

    @Module
    @InstallIn(SingletonComponent::class)
    class LocationModule {
        @Provides
        fun provideFusedLocationProviderClient(
            @ApplicationContext context: Context
        ): FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    }

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
    fun provideReloadFormValidatorUseCase(): ValidateReloadFormUseCase {
        return ValidateReloadFormUseCase(
            validDate = ValidateCardDateUseCase(),
            validateCVV = ValidateCVVUseCase()
        )
    }

    @Provides
    @Singleton
    fun provideValidateAboutUserUseCase(): ValidateAboutUserUseCase {
        return ValidateAboutUserUseCase()
    }

    @Provides
    @Singleton
    fun provideValidateReservationUseCase(): ValidateReservationUseCase {
        return ValidateReservationUseCase()
    }

}