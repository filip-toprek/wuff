package com.filiptoprek.wuff.navigation

sealed class Routes(val route: String) {
    object LandingScreen : Routes("LandingScreen")
    object Login : Routes("LoginScreen")
    object Register : Routes("RegisterScreen")
    object Home : Routes("HomeScreen")
    object Reservations : Routes("ReservationsScreen")
    object Profile : Routes("ProfileScreen")
}