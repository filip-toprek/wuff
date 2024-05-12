package com.filiptoprek.wuff

sealed class Routes(val route: String) {
    object LandingScreen : Routes("LandingScreen")
    object Login : Routes("LoginScreen")
    object Register : Routes("RegisterScreen")
    object Home : Routes("HomeScreen")
}