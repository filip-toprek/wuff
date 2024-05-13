package com.filiptoprek.wuff.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.filiptoprek.wuff.navigation.Routes
import com.filiptoprek.wuff.auth.presentation.AuthViewModel

@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: AuthViewModel?
){
    Column(
        modifier = Modifier
            .background(Color(android.graphics.Color.parseColor("#081C15")))
            .fillMaxSize()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .wrapContentHeight(Alignment.Top)
    ) {
        Text(text = "test", color = Color.White)
        Text(text = viewModel?.currentUser?.email ?: "ERROR", color = Color.White)
        Text(text = viewModel?.currentUser?.displayName ?: "ERROR", color = Color.White)
        Text(text = viewModel?.currentUser?.uid ?: "ERROR", color = Color.White)
        Button(onClick = {
            viewModel?.logout()
            navController.navigate(Routes.Login.route){
                popUpTo(Routes.Home.route) { inclusive = true }
            }
        }) {
            Text(text = "Odjavi se")
        }
    }
}