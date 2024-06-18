package com.filiptoprek.wuff.presentation.auth

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.filiptoprek.wuff.R
import com.filiptoprek.wuff.navigation.Routes
import com.filiptoprek.wuff.ui.theme.AppTheme
import com.filiptoprek.wuff.ui.theme.Opensans
import com.filiptoprek.wuff.ui.theme.Pattaya

@Composable
fun LandingScreen(
    navController : NavHostController
) {
    val activity = LocalView.current.context as Activity
    activity.window.statusBarColor = colorResource(R.color.background_dark).toArgb()
    Column(
        modifier = Modifier
            .background(colorResource(R.color.background_dark))
            .fillMaxSize()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .wrapContentHeight(Alignment.Top),
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally),
            text = "Wuff!",
            style = TextStyle(
                fontFamily = MaterialTheme.typography.titleLarge.fontFamily,
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                fontWeight = MaterialTheme.typography.titleLarge.fontWeight,
                textAlign = MaterialTheme.typography.titleLarge.textAlign,
                color = Color.White
            ),
        )
        Image(
            painterResource(R.drawable.dog), "dog image",
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally)
                .offset(0.dp, (-80).dp)
                .size(AppTheme.dimens.large)
        )

        Column(
            modifier = Modifier
                .background(colorResource(R.color.background_dark))
                .fillMaxSize()
                .wrapContentWidth(Alignment.CenterHorizontally)
                .wrapContentHeight(Alignment.Top)
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally),
                text = "Savršeno usklađena šetnja za tvojeg ljubimca.",
                style = TextStyle(
                    fontFamily = MaterialTheme.typography.bodyMedium.fontFamily,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                    textAlign = MaterialTheme.typography.bodyMedium.textAlign,
                    color = Color.White
                ),
            )
            Button(modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally)
                .padding(top = 60.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.green_accent)
                ),
                onClick = {
                    navController.navigate(Routes.Register.route)
                })
            {
                Text(
                    modifier = Modifier.padding(5.dp),
                    text = "Započni",
                    style = TextStyle(
                        fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
                        fontSize = MaterialTheme.typography.displayMedium.fontSize,
                        fontWeight = MaterialTheme.typography.displayMedium.fontWeight,
                        textAlign = MaterialTheme.typography.displayMedium.textAlign,
                        color = Color.White
                    ),
                )
            }
            Button(modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally)
                .padding(top = 20.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = androidx.compose.ui.graphics.Color.Transparent),
                onClick = {
                    navController.navigate(Routes.Login.route)
                })
            {
                Text(
                    modifier = Modifier.padding(5.dp),
                    text = "Prijavi se",
                    style = TextStyle(
                        fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
                        fontSize = MaterialTheme.typography.displayMedium.fontSize,
                        fontWeight = MaterialTheme.typography.displayMedium.fontWeight,
                        textAlign = MaterialTheme.typography.displayMedium.textAlign,
                        color = Color.White
                    ),
                )
            }
        }
    }
}