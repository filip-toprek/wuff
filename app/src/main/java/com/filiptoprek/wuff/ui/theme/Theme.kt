package com.filiptoprek.wuff.ui.theme

import android.app.Activity
import android.graphics.Color.parseColor
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.filiptoprek.wuff.presentation.auth.AuthViewModel
import com.google.firebase.auth.FirebaseUser

private val DarkColorScheme = darkColorScheme(
    primary = Purple40,
    secondary = Purple40,
    tertiary = Purple40,
    surface = Purple40,
    onPrimary = Purple40,
    onSecondary = Purple40,
    onTertiary = Purple40,
    onBackground = Purple40,
    onSurface = Purple40,
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = Purple40,
    tertiary = Purple40,
    background = Color.Black,

    surface = Purple40,
    onPrimary = Purple40,
    onSecondary = Purple40,
    onTertiary = Purple40,
    onBackground = Purple40,
    onSurface = Purple40,

)

@Composable
fun WuffTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
    authViewModel: AuthViewModel
) {
    val currentUser: FirebaseUser? by authViewModel.currentUserLiveData.observeAsState()
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (currentUser == null) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color(parseColor("#081C15")).toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }else
    {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color(parseColor("#ECECEC")).toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}