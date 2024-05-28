package com.filiptoprek.wuff.presentation.rating

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.outlined.StarRate
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.sharp.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun RatingBar(
    modifier: Modifier = Modifier,
    rating: Double = 0.0,
    stars: Int = 5,
    onRatingChanged: (Double) -> Unit,
    starsColor: Color = Color.Yellow
) {
    Row {
        for (index in 1..stars) {
            Icon(
                imageVector =
                if (index <= rating) {
                    Icons.Filled.StarRate
                } else {
                    Icons.Outlined.StarRate
                },
                contentDescription = null,
                tint = starsColor,
                modifier = modifier
                    .clickable { onRatingChanged(index.toDouble()) }
            )
        }
    }
}