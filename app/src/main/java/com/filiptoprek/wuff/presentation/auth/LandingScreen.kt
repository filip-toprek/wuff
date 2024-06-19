package com.filiptoprek.wuff.presentation.auth

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.filiptoprek.wuff.R
import com.filiptoprek.wuff.navigation.Routes
import com.filiptoprek.wuff.ui.theme.AppTheme

@Composable
fun LandingScreen(navController: NavHostController) {
    val activity = LocalView.current.context as Activity
    activity.window.statusBarColor = colorResource(R.color.background_dark).toArgb()

    Column(
        modifier = Modifier
            .background(colorResource(R.color.background_dark))
            .fillMaxSize()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .wrapContentHeight(Alignment.Top),
    ) {
        AuthTitle()
        LandingScreenImage()
        LandingScreenDescription()
        LandingScreenButtons(navController)
    }
}

@Composable
fun AuthTitle() {
    Text(
        text = "Wuff!",
        style = MaterialTheme.typography.titleLarge.copy(color = Color.White),
        modifier = Modifier
            .fillMaxWidth(),
        textAlign = TextAlign.Center
    )
}

@Composable
fun LandingScreenImage() {
    Image(
        painter = painterResource(R.drawable.dog),
        contentDescription = "dog image",
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .offset(0.dp, (-80).dp)
            .size(AppTheme.dimens.large)
    )
}

@Composable
fun LandingScreenDescription() {
    Text(
        text = "Savršeno usklađena šetnja za tvojeg ljubimca.",
        style = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally),
        textAlign = TextAlign.Center
    )
}

@Composable
fun LandingScreenButtons(navController: NavHostController) {
    Button(
        onClick = { navController.navigate(Routes.Register.route) },
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.green_accent)),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .padding(top = 60.dp),
    ) {
        Text(
            text = "Započni",
            style = MaterialTheme.typography.displayMedium.copy(color = Color.White),
            modifier = Modifier.padding(5.dp),
            textAlign = TextAlign.Center
        )
    }

    Button(
        onClick = { navController.navigate(Routes.Login.route) },
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .padding(top = 20.dp),
    ) {
        Text(
            text = "Prijavi se",
            style = MaterialTheme.typography.displayMedium.copy(color = Color.White),
            modifier = Modifier.padding(5.dp),
            textAlign = TextAlign.Center
        )
    }
}
