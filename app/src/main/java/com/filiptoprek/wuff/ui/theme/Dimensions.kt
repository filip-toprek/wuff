package com.filiptoprek.wuff.ui.theme

import android.content.res.Resources
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


data class Dimensions(
    val small:Dp,
    val smallMedium:Dp,
    val medium:Dp,
    val mediumLarge:Dp,
    val customSpacing:Dp,
    val largeText:Dp,
    val smallImage:Dp,
    val largeImage:Dp,
    val large:Dp
)

val smallDimensions = Dimensions(
    small = 2.dp,
    smallMedium = 4.dp,
    medium = 6.dp,
    mediumLarge = 9.dp,
    customSpacing = 6.dp,
    largeText = 10.dp,
    smallImage = 60.dp,
    largeImage = 100.dp,
    large = 150.dp
)

val compactDimensions = Dimensions(
    small = 3.dp,
    smallMedium = 5.dp,
    medium = 8.dp,
    mediumLarge = 11.dp,
    customSpacing = 8.dp,
    largeText = 15.dp,
    smallImage = 70.dp,
    largeImage = 100.dp,
    large = 200.dp
)

val mediumDimensions = Dimensions(
    small = 5.dp,
    smallMedium = 7.dp,
    medium = 10.dp,
    mediumLarge = 13.dp,
    customSpacing = 10.dp,
    largeText = 18.dp,
    smallImage = 80.dp,
    largeImage = 100.dp,
    large = 230.dp
)

val largeDimensions = if(hasNavBar()) Dimensions(
    small = 8.dp,
    smallMedium = 11.dp,
    medium = 15.dp,
    mediumLarge = 20.dp,
    customSpacing = 80.dp,
    largeText = 25.dp,
    smallImage = 100.dp,
    largeImage = 100.dp,
    large = 300.dp
) else Dimensions(
    small = 8.dp,
    smallMedium = 11.dp,
    medium = 15.dp,
    mediumLarge = 20.dp,
    customSpacing = 80.dp,
    largeText = 25.dp,
    smallImage = 100.dp,
    largeImage = 100.dp,
    large = 350.dp
)

fun hasNavBar(): Boolean {
    val id = Resources.getSystem().getIdentifier("config_showNavigationBar", "bool", "android")
    return id > 0 && Resources.getSystem().getBoolean(id)
}