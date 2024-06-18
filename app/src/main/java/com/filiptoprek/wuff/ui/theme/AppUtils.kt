package com.filiptoprek.wuff.ui.theme

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember

@Composable
fun ProvideAppUtils(
    dimensions: Dimensions,
    content: @Composable () ->Unit
) {
    val dimSet = remember{dimensions}
    CompositionLocalProvider(
        LocalAppDimens provides dimSet,
        LocalOrientationMode provides Configuration.ORIENTATION_PORTRAIT,
        content = content
    )
}

val LocalAppDimens = compositionLocalOf {
    smallDimensions
}

val LocalOrientationMode = compositionLocalOf {
    Configuration.ORIENTATION_PORTRAIT
}