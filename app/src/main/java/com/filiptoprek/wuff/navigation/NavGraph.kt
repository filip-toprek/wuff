package com.filiptoprek.wuff.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.filiptoprek.wuff.auth.presentation.AuthViewModel
import com.filiptoprek.wuff.auth.presentation.LoginScreen
import com.filiptoprek.wuff.ui.landing.LandingScreen
import com.filiptoprek.wuff.auth.presentation.RegisterScreen
import com.filiptoprek.wuff.ui.home.HomeScreen
import com.google.android.gms.auth.api.signin.GoogleSignInClient

@Composable
fun SetupNavGraph(
    navController: NavHostController = rememberNavController(),
    viewModel: AuthViewModel,
    googleSignInClient: GoogleSignInClient
){
    NavHost(navController = navController, startDestination =
        if(viewModel.currentUser == null){
           Routes.LandingScreen.route
        }else
        {
            Routes.Home.route
        }
    )
        {
        composable(
            route = Routes.LandingScreen.route
        ){
            LandingScreen(navController)
        }
        composable(
            route = Routes.Login.route
        ){
            LoginScreen(navController, viewModel, googleSignInClient)
        }
        composable(
            route = Routes.Register.route
        ){
            RegisterScreen(navController, viewModel, googleSignInClient)
        }
        composable(
            route = Routes.Home.route
        ){
            HomeScreen(navController, viewModel)
        }
    }
}