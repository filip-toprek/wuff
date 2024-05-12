package com.filiptoprek.wuff

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.filiptoprek.wuff.ui.auth.AuthViewModel
import com.filiptoprek.wuff.ui.auth.LoginScreen
import com.filiptoprek.wuff.ui.auth.LandingScreen
import com.filiptoprek.wuff.ui.auth.RegisterScreen
import com.filiptoprek.wuff.ui.home.HomeScreen

@Composable
fun SetupNavGraph(
    navController: NavHostController = rememberNavController(),
    viewModel: AuthViewModel
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
            LoginScreen(navController, viewModel)
        }
        composable(
            route = Routes.Register.route
        ){
            RegisterScreen(navController, viewModel)
        }
        composable(
            route = Routes.Home.route
        ){
            HomeScreen(navController, viewModel)
        }
    }
}