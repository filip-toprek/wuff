package com.filiptoprek.wuff

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.filiptoprek.wuff.navigation.SetupNavGraph
import com.filiptoprek.wuff.presentation.auth.AuthViewModel
import com.filiptoprek.wuff.presentation.home.HomeViewModel
import com.filiptoprek.wuff.presentation.profile.ProfileViewModel
import com.filiptoprek.wuff.presentation.rating.RatingViewModel
import com.filiptoprek.wuff.presentation.reload.ReloadViewModel
import com.filiptoprek.wuff.presentation.reservation.ReservationViewModel
import com.filiptoprek.wuff.ui.theme.WuffTheme
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val authViewModel by viewModels<AuthViewModel>()
    private val profileViewModel by viewModels<ProfileViewModel>()
    private val homeViewModel by viewModels<HomeViewModel>()
    private val reservationViewModel by viewModels<ReservationViewModel>()
    private val reloadViewModel by viewModels<ReloadViewModel>()
    private val ratingViewModel by viewModels<RatingViewModel>()

    @Inject
    lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WuffTheme {
                SetupNavGraph(viewModel = authViewModel, profileViewModel = profileViewModel, homeViewModel = homeViewModel, reservationViewModel = reservationViewModel, reloadViewModel = reloadViewModel, ratingViewModel = ratingViewModel, googleSignInClient = googleSignInClient)
            }
        }
    }
}