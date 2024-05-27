package com.filiptoprek.wuff.presentation.profile

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.filiptoprek.wuff.R
import com.filiptoprek.wuff.domain.model.profile.UserProfile
import com.filiptoprek.wuff.presentation.auth.AuthViewModel
import com.filiptoprek.wuff.ui.theme.Opensans

@Preview
@Composable
fun userProfileScreen(userProfile: UserProfile)
{
    Column(
        modifier = Modifier
            .wrapContentWidth(Alignment.CenterHorizontally)
            .wrapContentHeight(Alignment.Top),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AsyncImage(
            modifier = Modifier
                .clip(RoundedCornerShape(90.dp))
                .border(
                    1.dp,
                    colorResource(R.color.gray),
                    shape = RoundedCornerShape(90.dp)
                )
                .size(100.dp),
            model = userProfile.user.profilePhotoUrl,
            placeholder = painterResource(id = R.drawable.user_placeholder),
            error = painterResource(id = R.drawable.user_placeholder),
            contentDescription = "User image",
        )
        Spacer(modifier = Modifier.size(25.dp))
        Text(
            text = userProfile.user.name ?: "Ime i prezime",
            style = TextStyle(
                fontFamily = Opensans,
                fontSize = 18.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
            ),
            color = colorResource(R.color.gray)
        )
        Spacer(modifier = Modifier.size(10.dp))
        if (userProfile.walker != null) {
            Text(
                text = userProfile.walker.averageRating.toString() + "/5.0",
                style = TextStyle(
                    fontFamily = Opensans,
                    fontSize = 12.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Thin,
                    textAlign = TextAlign.Center,
                ),
                color = colorResource(R.color.green_accent)
            )
        }
        Spacer(modifier = Modifier.size(15.dp))
        profileData(userProfile, false)
    }
}