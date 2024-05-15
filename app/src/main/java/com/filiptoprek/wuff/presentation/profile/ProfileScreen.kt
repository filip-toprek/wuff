package com.filiptoprek.wuff.presentation.profile

import android.widget.Space
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import arrow.core.raise.result
import coil.compose.AsyncImage
import com.filiptoprek.wuff.R
import com.filiptoprek.wuff.domain.model.auth.Resource
import com.filiptoprek.wuff.domain.model.profile.UserProfile
import com.filiptoprek.wuff.navigation.Routes
import com.filiptoprek.wuff.presentation.auth.AuthViewModel
import com.filiptoprek.wuff.presentation.core.ProfileViewModel
import com.filiptoprek.wuff.ui.theme.Opensans
import com.filiptoprek.wuff.ui.theme.Pattaya
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavHostController,
    viewModel: AuthViewModel?,
    profileViewModel: ProfileViewModel?
){
    viewModel?.let { authViewModel ->
        profileViewModel?.let { profileVM ->
            userProfile(navController, authViewModel, profileVM)
        }
    }
}

@Composable
fun userProfile(
    navController: NavHostController = rememberNavController(),
    viewModel: AuthViewModel?,
    profileViewModel: ProfileViewModel?
){
    var isEdting by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .background(colorResource(R.color.background_white))
            .fillMaxSize()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .wrapContentHeight(Alignment.Top)
    ) {
        Row {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .padding(top = 15.dp),
                text = "Wuff!",
                style = TextStyle(
                    fontFamily = Pattaya,
                    fontSize = 50.sp,
                    lineHeight = 27.sp,
                    color = colorResource(R.color.green_accent)
                )
            )
        }
        Spacer(modifier = Modifier.size(100.dp))
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
                    .border(1.dp, colorResource(R.color.gray), shape = RoundedCornerShape(90.dp))
                    .size(100.dp),
                model = viewModel?.currentUser?.photoUrl,
                placeholder = painterResource(id = R.drawable.user_placeholder),
                error = painterResource(id = R.drawable.user_placeholder),
                contentDescription = "User image",
            )
            Spacer(modifier = Modifier.size(25.dp))
            Text(
                text = viewModel?.currentUser?.displayName ?: "Ime i prezime",
                style = TextStyle(
                    fontFamily = Opensans,
                    fontSize = 18.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                ),
                color = colorResource(R.color.gray)
            )
            Spacer(modifier = Modifier.size(35.dp))

            if (!isEdting){
                profileData(profileViewModel)

                Button(modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .width(IntrinsicSize.Max),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.green_accent)
                    ),
                    onClick = {
                        isEdting = true
                    })
                {
                    Text(
                        modifier = Modifier,
                        text = "Izmijeni",
                        style = TextStyle(
                            fontFamily = Opensans,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = Color.White
                        )
                    )
                }
                Spacer(modifier = Modifier.size(5.dp))
                Button(modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .width(IntrinsicSize.Max),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.green_accent)
                    ),
                    onClick = {
                        viewModel?.viewModelScope?.launch {
                            viewModel.logout()
                            navController.navigate(Routes.Login.route) {
                                popUpTo(Routes.Home.route) { inclusive = true }
                            }
                        }
                    })
                {
                    Text(
                        modifier = Modifier,
                        text = "Odjavi se",
                        style = TextStyle(
                            fontFamily = Opensans,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = Color.White
                        )
                    )
                }
        }else
        {
            var aboutUser by remember { mutableStateOf(profileViewModel?.userProfile?.aboutUser.toString()) }
            val onAboutUserChanged: (String) -> Unit = { newValue ->
                aboutUser = newValue
            }
            editingModal(profileViewModel, aboutUser, onAboutUserChanged, isError)
            Button(modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally)
                .wrapContentHeight(Alignment.CenterVertically)
                .width(IntrinsicSize.Max),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.green_accent)
                ),
                onClick = {
                    isError = !profileViewModel?.updateUserProfile(UserProfile(aboutUser = aboutUser))!!
                    if(!isError)
                    {
                        isEdting = false
                    }
                })
            {
                Text(
                    modifier = Modifier,
                    text = "Spremi",
                    style = TextStyle(
                        fontFamily = Opensans,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                )
            }
        }
        }
    }

}

@Composable
fun profileData(profileViewModel: ProfileViewModel?)
{
    var isLoading by remember { mutableStateOf(false) }
    val profileFlow = profileViewModel?.profileFlow?.collectAsState()

    profileFlow?.value?.let {
        when(it){
            is Resource.Failure -> {
                isLoading = false
            }
            Resource.Loading -> {
                isLoading = true
            }
            is Resource.Success -> {
                isLoading = false
            }
        }
    }

    if(isLoading){
        CircularProgressIndicator(modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .wrapContentHeight(Alignment.CenterVertically),
            color = colorResource(R.color.green_accent)
        )
    }else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth()
                .padding(15.dp)
                .background(colorResource(R.color.box_bkg_white), RoundedCornerShape(8.dp))
                .padding(15.dp)
                .height(IntrinsicSize.Min)
        ) {
            Column {
                Text(
                    text = "Saldo",
                    color = colorResource(R.color.gray),
                    style = TextStyle(
                        fontFamily = Opensans,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                )
                Text(
                    modifier = Modifier.align(Alignment.End),
                    text = profileViewModel?.userProfile?.balance.toString(),
                    color = colorResource(R.color.gray),
                    style = TextStyle(
                        fontFamily = Opensans,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                )
                Spacer(modifier = Modifier.size(20.dp))
                Text(
                    text = "Broj šetnji",
                    color = colorResource(R.color.gray),
                    style = TextStyle(
                        fontFamily = Opensans,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                )
                Text(
                    modifier = Modifier.align(Alignment.End),
                    text = profileViewModel?.userProfile?.numOfWalks.toString(),
                    color = colorResource(R.color.gray),
                    style = TextStyle(
                        fontFamily = Opensans,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                )
            }
            Spacer(modifier = Modifier.size(20.dp))
            Divider(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(1.dp),
                color = colorResource(R.color.gray),
            )
            Spacer(modifier = Modifier.size(20.dp))
            Column {
                Text(
                    text = "O meni",
                    color = colorResource(R.color.gray),
                    style = TextStyle(
                        fontFamily = Opensans,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                )
                Text(
                    modifier = Modifier
                        .align(Alignment.Start)
                        .fillMaxWidth(0.4f),
                    text = profileViewModel?.userProfile?.aboutUser.toString(),
                    color = colorResource(R.color.gray),
                    style = TextStyle(
                        fontFamily = Opensans,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Start,
                        color = Color.White
                    )
                )
            }

        }
    }
}

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun editingModal(profileViewModel: ProfileViewModel?, aboutUser: String, onAboutUserChanged: (String) -> Unit, isError: Boolean)
{
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth()
            .padding(15.dp)
            .background(colorResource(R.color.box_bkg_white), RoundedCornerShape(8.dp))
            .padding(15.dp)
            .height(IntrinsicSize.Min)
    ) {
        Column {
            Text(
                text = "O meni",
                color = colorResource(R.color.gray),
                style = TextStyle(
                    fontFamily = Opensans,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
            )
            Spacer(modifier = Modifier.size(5.dp))
            TextField(
                modifier = Modifier.align(Alignment.End),
                value = aboutUser,
                textStyle = TextStyle(
                    fontFamily = Opensans,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Start,
                    color = colorResource(R.color.gray),
                ),
                supportingText = {
                    if (isError) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Opis može biti najviše 50 znakova.",
                            color = Color.Red
                        )
                    }
                },
                onValueChange = {
                    onAboutUserChanged(it)
                },
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color(android.graphics.Color.parseColor("#333333")),
                    containerColor  = Color.White,
                    cursorColor = Color(android.graphics.Color.parseColor("#52B788")),
                    disabledLabelColor = Color.Transparent,
                    focusedIndicatorColor =  Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent),
            )
        }
    }
}