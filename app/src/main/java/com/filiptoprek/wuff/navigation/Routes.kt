package com.filiptoprek.wuff.navigation

sealed class Routes(val route: String) {
    object LandingScreen : Routes("LandingScreen")
    object Login : Routes("LoginScreen")
    object Register : Routes("RegisterScreen")
    object Home : Routes("HomeScreen")
    object Reservations : Routes("ReservationsScreen")
    object Profile : Routes("ProfileScreen")
    object UserProfile : Routes("UserProfileScreen")
    object ReservationDetails : Routes("ReservationDetailsScreen")
    object RateWalker : Routes("RateWalkerScreen")
    object ResereveWalk : Routes("ReserveWalkScreen")
    object Reload : Routes("ReloadScreen")
    object TrackLocation : Routes("LocationScreen")
}