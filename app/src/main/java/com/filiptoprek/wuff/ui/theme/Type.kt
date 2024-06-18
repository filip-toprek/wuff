package com.filiptoprek.wuff.ui.theme

import android.annotation.SuppressLint
import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.filiptoprek.wuff.R


val Pattaya = FontFamily(
    Font(R.font.pattaya)
)
val Opensans = FontFamily(
    Font(R.font.opensans)
)
// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)

@SuppressLint("ResourceAsColor")
val typographySmall = Typography(
    bodySmall = TextStyle(
        fontFamily = Opensans,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        textAlign = TextAlign.Center,
        color = Color(R.color.background_dark)
    ),
    labelSmall = TextStyle(
        fontFamily = Opensans,
        fontWeight = FontWeight.Thin,
        fontSize = 14.sp,
        textAlign = TextAlign.Start,
        color = Color(R.color.gray)
    ),
    bodyMedium = TextStyle(
        fontFamily = Opensans,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        textAlign = TextAlign.Center,
        color = Color(R.color.background_dark)
    ),
    bodyLarge = TextStyle(
        fontFamily = Opensans,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        textAlign = TextAlign.Center,
        color = Color(R.color.background_dark)
    ),
    titleLarge = TextStyle(
        fontFamily = Pattaya,
        fontSize = 85.sp,
        lineHeight = 27.sp,
        color = Color(R.color.background_dark)
    ),
    labelMedium =  TextStyle(
        fontFamily = Opensans,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = Color(R.color.background_dark)
    ),
    labelLarge = TextStyle(
        fontFamily = Opensans,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = Color(R.color.background_dark)
    ),
    headlineMedium = TextStyle(
        fontFamily = Opensans,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = Color(R.color.background_dark)
    ),
    headlineLarge = TextStyle(
        fontFamily = Opensans,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = Color(R.color.background_dark)
    ),
    headlineSmall = TextStyle(
        fontFamily = Opensans,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = Color(R.color.background_dark)
    ),
    displayLarge = TextStyle(
        fontFamily = Opensans,
        fontSize = 52.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = Color(R.color.background_dark)
    ),
    displaySmall = TextStyle(
        fontFamily = Opensans,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = Color(R.color.background_dark)
    ),
    titleMedium = TextStyle(
        fontFamily = Opensans,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = Color(R.color.background_dark)
    ),
    titleSmall = TextStyle(
        fontFamily = Opensans,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = Color(R.color.background_dark)
    ),
)

@SuppressLint("ResourceAsColor")
val typographyCompact = Typography(
    bodySmall = TextStyle(
        fontFamily = Opensans,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        textAlign = TextAlign.Center,
        color = Color(R.color.background_dark)
    ),
    labelSmall = TextStyle(
        fontFamily = Opensans,
        fontWeight = FontWeight.Thin,
        fontSize = 14.sp,
        textAlign = TextAlign.Start,
        color = Color(R.color.gray)
    ),
    bodyMedium = TextStyle(
        fontFamily = Opensans,
        fontWeight = FontWeight.Normal,
        fontSize = 17.sp,
        textAlign = TextAlign.Center,
        color = Color(R.color.background_dark)
    ),
    bodyLarge = TextStyle(
        fontFamily = Opensans,
        fontWeight = FontWeight.Normal,
        fontSize = 21.sp,
        textAlign = TextAlign.Center,
        color = Color(R.color.background_dark)
    ),
    titleLarge = TextStyle(
        fontFamily = Pattaya,
        fontSize = 95.sp,
        lineHeight = 27.sp,
        color = Color(R.color.background_dark)
    ),
    labelMedium =  TextStyle(
        fontFamily = Opensans,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = Color(R.color.background_dark)
    ),
    labelLarge = TextStyle(
        fontFamily = Opensans,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = Color(R.color.background_dark)
    ),
    headlineMedium = TextStyle(
        fontFamily = Opensans,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = Color(R.color.background_dark)
    ),
    headlineLarge = TextStyle(
        fontFamily = Opensans,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = Color(R.color.background_dark)
    ),
    headlineSmall = TextStyle(
        fontFamily = Opensans,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = Color(R.color.background_dark)
    ),
    displayLarge = TextStyle(
        fontFamily = Opensans,
        fontSize = 52.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = Color(R.color.background_dark)
    ),
    displaySmall = TextStyle(
        fontFamily = Opensans,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = Color(R.color.background_dark)
    ),
    titleMedium = TextStyle(
        fontFamily = Opensans,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = Color(R.color.background_dark)
    ),
    titleSmall = TextStyle(
        fontFamily = Opensans,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = Color(R.color.background_dark)
    ),
)

@SuppressLint("ResourceAsColor")
val typographyMedium = Typography(
    bodySmall = TextStyle(
        fontFamily = Opensans,
        fontWeight = FontWeight.Thin,
        fontSize = 14.sp,
        textAlign = TextAlign.Center,
        color = Color(R.color.background_dark)
    ),
    labelSmall = TextStyle(
        fontFamily = Opensans,
        fontWeight = FontWeight.Thin,
        fontSize = 14.sp,
        textAlign = TextAlign.Start,
        color = Color(R.color.gray)
    ),
    bodyMedium = TextStyle(
        fontFamily = Opensans,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 17.sp,
        textAlign = TextAlign.Center,
        color = Color(R.color.background_dark)
    ),
    bodyLarge = TextStyle(
        fontFamily = Opensans,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        textAlign = TextAlign.Center,
        color = Color(R.color.background_dark)
    ),
    titleLarge = TextStyle(
        fontFamily = Pattaya,
        fontSize = 110.sp,
        lineHeight = 17.sp,
        color = Color(R.color.background_dark)
    ),
    displayMedium = TextStyle(
        fontFamily = Opensans,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = Color(R.color.background_dark)
    ),
    labelMedium =  TextStyle(
        fontFamily = Opensans,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = Color(R.color.background_dark)
    ),
    labelLarge = TextStyle(
        fontFamily = Opensans,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = Color(R.color.background_dark)
    ),
    headlineMedium = TextStyle(
        fontFamily = Opensans,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = Color(R.color.background_dark)
    ),
    headlineLarge = TextStyle(
        fontFamily = Opensans,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = Color(R.color.background_dark)
    ),
    headlineSmall = TextStyle(
        fontFamily = Opensans,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = Color(R.color.background_dark)
    ),
    displayLarge = TextStyle(
        fontFamily = Opensans,
        fontSize = 52.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = Color(R.color.background_dark)
    ),
    displaySmall = TextStyle(
        fontFamily = Opensans,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = Color(R.color.background_dark)
    ),
    titleMedium = TextStyle(
        fontFamily = Opensans,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = Color(R.color.background_dark)
    ),
    titleSmall = TextStyle(
        fontFamily = Opensans,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = Color(R.color.background_dark)
    ),
)

@SuppressLint("ResourceAsColor")
val typographyBig = Typography(
    bodySmall = TextStyle(
        fontFamily = Opensans,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        textAlign = TextAlign.Center,
        color = Color(R.color.background_dark)
    ),
    labelSmall = TextStyle(
        fontFamily = Opensans,
        fontWeight = FontWeight.Thin,
        fontSize = 14.sp,
        textAlign = TextAlign.Start,
        color = Color(R.color.background_dark)
    ),
    bodyMedium = TextStyle(
        fontFamily = Opensans,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        lineHeight = 24.sp,
        textAlign = TextAlign.Center,
        color = Color(R.color.background_dark)
    ),
    bodyLarge = TextStyle(
        fontFamily = Opensans,
        fontWeight = FontWeight.Normal,
        fontSize = 17.sp,
        textAlign = TextAlign.Center,
        color = Color(R.color.background_dark)
    ),
    titleLarge = TextStyle(
        fontFamily = Pattaya,
        fontSize = 135.sp,
        lineHeight = 27.sp,
        color = Color(R.color.background_dark)
    ),
    displayMedium = TextStyle(
        fontFamily = Opensans,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = Color(R.color.background_dark)
    ),
    labelMedium =  TextStyle(
        fontFamily = Opensans,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = Color(R.color.background_dark)
    ),
    labelLarge = TextStyle(
        fontFamily = Opensans,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = Color(R.color.background_dark)
    ),
    headlineMedium = TextStyle(
        fontFamily = Opensans,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = Color(R.color.background_dark)
    ),
    headlineLarge = TextStyle(
        fontFamily = Opensans,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = Color(R.color.background_dark)
    ),
    headlineSmall = TextStyle(
        fontFamily = Opensans,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = Color(R.color.background_dark)
    ),
    displayLarge = TextStyle(
        fontFamily = Opensans,
        fontSize = 52.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = Color(R.color.background_dark)
    ),
    displaySmall = TextStyle(
        fontFamily = Opensans,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = Color(R.color.background_dark)
    ),
    titleMedium = TextStyle(
        fontFamily = Opensans,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = Color(R.color.background_dark)
    ),
    titleSmall = TextStyle(
        fontFamily = Opensans,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = Color(R.color.background_dark)
    ),
)