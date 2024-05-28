package com.filiptoprek.wuff.navigation

sealed class Routes(val route: String) {
    object LandingScreen : Routes("LandingScreen")
    object Login : Routes("LoginScreen")
    object Register : Routes("RegisterScreen")
    object Home : Routes("HomeScreen")
    object Reservations : Routes("ReservationsScreen")
    object Profile : Routes("ProfileScreen")
    object userProfile : Routes("UserProfileScreen")
    object reservationDetails : Routes("ReservationDetailsScreen")
    object rateWalker : Routes("RateWalkerScreen")
    object resereveWalk : Routes("ReserveWalkScreen")
}