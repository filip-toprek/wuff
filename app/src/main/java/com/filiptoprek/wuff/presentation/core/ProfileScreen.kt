package com.filiptoprek.wuff.presentation.core

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.filiptoprek.wuff.R
import com.filiptoprek.wuff.navigation.Routes
import com.filiptoprek.wuff.presentation.auth.AuthViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavHostController,
    viewModel: AuthViewModel?
){
        Column(
        ) {
            AsyncImage(
                modifier = Modifier.clip(RoundedCornerShape(30.dp)),
                model = viewModel?.currentUser?.photoUrl,
                placeholder = painterResource(id = R.drawable.user_placeholder),
                error = painterResource(id = R.drawable.user_placeholder),
                contentDescription = "User image",
            )
            Text(text = viewModel?.currentUser?.email ?: "", color = Color.White)
            Text(text = viewModel?.currentUser?.displayName ?: "", color = Color.White)
            Text(text = viewModel?.currentUser?.uid ?: "", color = Color.White)
            Button(onClick = {
                viewModel?.viewModelScope?.launch {
                    viewModel.logout()
                    navController.navigate(Routes.Login.route) {
                        popUpTo(Routes.Home.route) { inclusive = true }
                    }
                }
            }) {
                Text(text = "Odjavi se")
            }
        }
}