package com.filiptoprek.wuff.navigation

import android.content.Context
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.filiptoprek.wuff.R
import com.filiptoprek.wuff.presentation.auth.AuthViewModel
import com.filiptoprek.wuff.presentation.auth.LoginScreen
import com.filiptoprek.wuff.presentation.auth.LandingScreen
import com.filiptoprek.wuff.presentation.auth.RegisterScreen
import com.filiptoprek.wuff.domain.model.core.BottomNavigationItem
import com.filiptoprek.wuff.presentation.home.HomeScreen
import com.filiptoprek.wuff.presentation.home.HomeViewModel
import com.filiptoprek.wuff.presentation.location.LocationScreen
import com.filiptoprek.wuff.presentation.location.LocationViewModel
import com.filiptoprek.wuff.presentation.profile.ProfileViewModel
import com.filiptoprek.wuff.presentation.profile.ProfileScreen
import com.filiptoprek.wuff.presentation.profile.userProfileScreen
import com.filiptoprek.wuff.presentation.rating.RateWalkerScreen
import com.filiptoprek.wuff.presentation.rating.RatingViewModel
import com.filiptoprek.wuff.presentation.reload.ReloadScreen
import com.filiptoprek.wuff.presentation.reload.ReloadViewModel
import com.filiptoprek.wuff.presentation.reservation.ReservationDetailsScreen
import com.filiptoprek.wuff.presentation.reservation.ReservationViewModel
import com.filiptoprek.wuff.presentation.reservation.ReservationsScreen
import com.filiptoprek.wuff.presentation.reservation.ReserveWalkScreen
import com.filiptoprek.wuff.presentation.shared.SharedViewModel
import com.filiptoprek.wuff.presentation.withdraw.WithdrawRequestScreen
import com.filiptoprek.wuff.presentation.withdraw.WithdrawViewModel
import com.filiptoprek.wuff.presentation.withdraw.WithdrawalScreen
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.maps.GoogleMap
import com.google.firebase.auth.FirebaseUser

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupNavGraph(
    navController: NavHostController = rememberNavController(),
    viewModel: AuthViewModel,
    profileViewModel: ProfileViewModel,
    homeViewModel: HomeViewModel,
    reservationViewModel: ReservationViewModel,
    reloadViewModel: ReloadViewModel,
    ratingViewModel: RatingViewModel,
    sharedViewModel: SharedViewModel,
    locationViewModel: LocationViewModel,
    withdrawViewModel: WithdrawViewModel,
    googleSignInClient: GoogleSignInClient
) {
    val items = listOf(
        BottomNavigationItem(
            title = "Početna",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            hasNews = false,
            route = Routes.Home.route
        ),
        BottomNavigationItem(
            title = "Rezervacije",
            selectedIcon = Icons.Filled.CheckCircle,
            unselectedIcon = Icons.Outlined.CheckCircle,
            hasNews = false,
            route = Routes.Reservations.route
        ),
        BottomNavigationItem(
            title = "Profil",
            selectedIcon = Icons.Filled.Person,
            unselectedIcon = Icons.Outlined.Person,
            hasNews = false,
            route = Routes.Profile.route
        ),
    )
    var selectedItemIndex by rememberSaveable {
        mutableStateOf(0)
    }
    val currentUser: FirebaseUser? by viewModel.currentUserLiveData.observeAsState()

    Scaffold(
        modifier = Modifier.fillMaxWidth(),
        containerColor = colorResource(R.color.background_white),
        bottomBar = {
            if (currentUser != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    NavigationBar(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50.dp, 50.dp, 50.dp, 50.dp))
                            .width(200.dp),
                        containerColor = colorResource(R.color.gray),
                        contentColor = colorResource(R.color.green_accent),
                    ) {
                        items.forEachIndexed { index, item ->
                            NavigationBarItem(
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = colorResource(R.color.green_accent),
                                    unselectedIconColor = Color(255, 255, 255, 50),
                                    indicatorColor = colorResource(R.color.gray)
                                ),
                                selected = index == selectedItemIndex,
                                onClick = {
                                    selectedItemIndex = index
                                    navController.navigate(item.route) {
                                        popUpTo(item.route) { inclusive = true }
                                    }
                                },
                                alwaysShowLabel = false,
                                icon = {
                                    BadgedBox(
                                        badge = {
                                            if (item.badgeCount != null) {
                                                Badge {
                                                    Text(text = item.badgeCount.toString())
                                                }
                                            } else if (item.hasNews) {
                                                Badge()
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = if (selectedItemIndex == index) {
                                                item.selectedIcon
                                            } else item.unselectedIcon,
                                            contentDescription = item.title,
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination =
            if (viewModel.currentUser == null) {
                selectedItemIndex = 0
                Routes.LandingScreen.route
            } else {
                Routes.Home.route
            },
            modifier = Modifier.padding(paddingValues)
        )
        {
            composable(
                route = Routes.LandingScreen.route
            ) {
                LandingScreen(navController)
            }
            composable(
                route = Routes.Login.route
            ) {
                LoginScreen(navController, viewModel, googleSignInClient)
            }
            composable(
                route = Routes.Register.route
            ) {
                RegisterScreen(navController, viewModel, googleSignInClient)
            }
            composable(
                route = Routes.Home.route
            ) {
                HomeScreen(navController, homeViewModel, profileViewModel, sharedViewModel, locationViewModel)
                BackHandler(true) {
                }
            }
            composable(
                route = Routes.Profile.route
            ) {
                ProfileScreen(navController, viewModel, profileViewModel)
                BackHandler(true) {
                }
            }
            composable(
                route = Routes.Reservations.route
            ) {
                ReservationsScreen(navController, reservationViewModel, profileViewModel, sharedViewModel)
                BackHandler(true) {
                }
            }
            composable(
                route = Routes.UserProfile.route
            ){
                userProfileScreen(sharedViewModel.userProfile!!)
            }
            composable(
                route = Routes.ReservationDetails.route
            ){
                ReservationDetailsScreen(sharedViewModel.selectedReservation!!, reservationViewModel, navController, viewModel, locationViewModel, sharedViewModel)
            }
            composable(
                route = Routes.RateWalker.route
            ){
                RateWalkerScreen(navController, ratingViewModel, viewModel, sharedViewModel.reservationToRate!!, reservationViewModel)
            }
            composable(
                route = Routes.ResereveWalk.route
            ){
                ReserveWalkScreen(reservationViewModel, sharedViewModel, navController)
            }
            composable(
                route = Routes.Reload.route
            ){
                ReloadScreen(reloadViewModel, navController)
            }
            composable(
                route = Routes.TrackLocation.route
            ){
                LocationScreen(navController, locationViewModel, sharedViewModel)
            }
            composable(
                route = Routes.Withdrawals.route
            ){
                WithdrawalScreen(navController, withdrawViewModel)
            }
            composable(
                route = Routes.CreateWithdraw.route
            ){
                WithdrawRequestScreen(navController, withdrawViewModel)
            }
        }
    }
}