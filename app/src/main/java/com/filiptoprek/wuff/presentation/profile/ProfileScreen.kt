package com.filiptoprek.wuff.presentation.profile

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.filiptoprek.wuff.R
import com.filiptoprek.wuff.domain.model.auth.Resource
import com.filiptoprek.wuff.domain.model.profile.UserProfile
import com.filiptoprek.wuff.domain.model.profile.Walker
import com.filiptoprek.wuff.navigation.Routes
import com.filiptoprek.wuff.presentation.auth.AuthViewModel
import com.filiptoprek.wuff.presentation.home.AppTitle
import com.filiptoprek.wuff.service.LocationService
import com.filiptoprek.wuff.ui.theme.AppTheme
import com.filiptoprek.wuff.ui.theme.Opensans
import com.filiptoprek.wuff.ui.theme.Pattaya
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavHostController,
    viewModel: AuthViewModel?,
    profileViewModel: ProfileViewModel?
){
    viewModel?.let { authViewModel ->
        profileViewModel?.let { profileVM ->
            var refreshing by remember { mutableStateOf(false) }

            LaunchedEffect(refreshing) {
                if (refreshing) {
                    profileVM.refreshUser()
                    delay(2000)
                    refreshing = false
                }
            }

            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing = refreshing),
                onRefresh = { refreshing = true },
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxHeight()
                ) {
                    item {
                        userProfile(navController, authViewModel, profileVM)
                    }
                }
            }
        }
    }
}

@Composable
fun userProfile(
    navController: NavHostController = rememberNavController(),
    viewModel: AuthViewModel?,
    profileViewModel: ProfileViewModel?
){
    var isEditing by remember { mutableStateOf(false) }
    var isApplying by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    val profileFlow = profileViewModel?.profileFlow?.collectAsState()
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("WuffPreferences", Context.MODE_PRIVATE)

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
    Column(
        modifier = Modifier
            .background(colorResource(R.color.background_white))
            .fillMaxSize()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .wrapContentHeight(Alignment.Top)
    ) {
        AppTitle()
        Spacer(modifier = Modifier.size(AppTheme.dimens.mediumLarge))
        if(isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .wrapContentHeight(Alignment.CenterVertically),
                color = colorResource(R.color.green_accent)
            )
        }else {
            Column(
                modifier = Modifier
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .wrapContentHeight(Alignment.Top),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if(!isApplying)
                {
                    AsyncImage(
                        modifier = Modifier
                            .clip(RoundedCornerShape(90.dp))
                            .border(
                                1.dp,
                                colorResource(R.color.gray),
                                shape = RoundedCornerShape(90.dp)
                            )
                            .size(AppTheme.dimens.smallImage),
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
                    Spacer(modifier = Modifier.size(10.dp))
                    if(profileViewModel?.userProfile?.walker != null && profileViewModel.userProfile?.walker?.approved == true)
                    {
                        Text(
                            text = profileViewModel.userProfile?.walker?.averageRating.toString() + "/5.0",
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
                }

                if (!isEditing && !isApplying && profileViewModel?.userProfile != null) {
                    ProfileData(profileViewModel.userProfile!!)

                    Button(modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                        .width(IntrinsicSize.Max),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.green_accent)
                        ),
                        onClick = {
                            isEditing = true
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
                            val isWalking = sharedPreferences.getBoolean("isWalking", false)
                            if(!isWalking || profileViewModel.userProfile?.walker?.approved == false)
                            {
                                Intent(context, LocationService::class.java).apply {
                                    action = LocationService.ACTION_STOP
                                    context.startService(this)
                                }
                                viewModel?.logout()
                                navController.navigate(Routes.Login.route) {
                                    popUpTo(Routes.LandingScreen.route) { inclusive = true }
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
                    Spacer(modifier = Modifier.size(5.dp))
                    if (profileViewModel?.userProfile?.walker == null){
                        Button(modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally)
                            .width(IntrinsicSize.Max),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorResource(R.color.green_accent)
                            ),
                            onClick = {
                                isApplying = true
                            })
                        {
                            Text(
                                modifier = Modifier,
                                text = "Postani šetač",
                                style = TextStyle(
                                    fontFamily = Opensans,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = Color.White
                                )
                            )
                        }
                    }else if(profileViewModel?.userProfile?.walker?.approved == false)
                    {
                        Text(
                            modifier = Modifier,
                            text = "Vaš račun trenutno čeka potvrdu",
                            style = TextStyle(
                                fontFamily = Opensans,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Thin,
                                textAlign = TextAlign.Center,
                                color = colorResource(R.color.gray)
                            )
                        )
                    }else if (profileViewModel?.userProfile?.walker?.approved == true)
                    {
                        Text(
                            modifier = Modifier,
                            text = "Vaš račun je potvrđen",
                            style = TextStyle(
                                fontFamily = Opensans,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Thin,
                                textAlign = TextAlign.Center,
                                color = colorResource(R.color.green_accent)
                            )
                        )
                    }
            }else if(isApplying && !isEditing)
                {
                    val onApplied: (Boolean) -> Unit = { newValue ->
                        isApplying = newValue
                    }
                    BecomeWalker(profileViewModel, onApplied)
                }else
            {
                var aboutUser by remember { mutableStateOf(profileViewModel?.userProfile?.aboutUser.toString()) }
                val onAboutUserChanged: (String) -> Unit = { newValue ->
                    aboutUser = newValue
                }
                EditingModal(profileViewModel, aboutUser, onAboutUserChanged, isError)
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
                            isEditing = false
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

}

@Composable
fun ProfileData(user: UserProfile, isProfile: Boolean = true)
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
            if(isProfile)
            {
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
                    text = user.balance.toString(),
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
            }
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
                text = user.numOfWalks.toString(),
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
                text = user.aboutUser.toString(),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditingModal(profileViewModel: ProfileViewModel?, aboutUser: String, onAboutUserChanged: (String) -> Unit, isError: Boolean)
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
                    unfocusedTextColor = Color(android.graphics.Color.parseColor("#333333")),
                    containerColor  = Color.White,
                    cursorColor = Color(android.graphics.Color.parseColor("#52B788")),
                    disabledLabelColor = Color.Transparent,
                    focusedIndicatorColor =  Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent),
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BecomeWalker(profileViewModel: ProfileViewModel?, onApplied: (Boolean) -> Unit){

        var phoneNumber by remember { mutableStateOf("") }
        var address by remember { mutableStateOf("") }
        var city by remember { mutableStateOf("") }
        var zipCode by remember { mutableStateOf("") }

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
                    text = "Postani šetač",
                    style = TextStyle(
                        fontFamily = Opensans,
                        fontSize = 23.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    ),
                    color = colorResource(R.color.gray)

                )
                Text(
                    text = "Trebamo malo više podataka o tebi",
                    style = TextStyle(
                        fontFamily = Opensans,
                        fontSize = 18.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Thin,
                        textAlign = TextAlign.Center,
                    ),
                    color = colorResource(R.color.gray)

                )
                Spacer(modifier = Modifier.size(20.dp))
                TextField(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    value = phoneNumber,
                    textStyle = MaterialTheme.typography.labelSmall,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    ),
                    label = { Text("Broj mobitela",
                        color = colorResource(R.color.gray),
                        style = MaterialTheme.typography.bodySmall) },
                    supportingText = {

                    },
                    onValueChange = {newValue ->
                        if (newValue.all { it.isDigit() } && newValue.length <= 10) {
                            phoneNumber = newValue
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        unfocusedTextColor = Color(android.graphics.Color.parseColor("#333333")),
                        containerColor  = Color.White,
                        cursorColor = Color(android.graphics.Color.parseColor("#52B788")),
                        disabledLabelColor = Color.Transparent,
                        focusedIndicatorColor =  Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent),
                )

                TextField(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    value = address,
                    textStyle = MaterialTheme.typography.labelSmall,
                    label = { Text("Adresa i broj",
                        color = colorResource(R.color.gray),
                        style = MaterialTheme.typography.bodySmall) },
                    supportingText = {
                        /*if (isError) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = "Opis može biti najviše 50 znakova.",
                                color = Color.Red
                            )
                        }*/
                    },
                    onValueChange = {
                        address = it
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        unfocusedTextColor = Color(android.graphics.Color.parseColor("#333333")),
                        containerColor  = Color.White,
                        cursorColor = Color(android.graphics.Color.parseColor("#52B788")),
                        disabledLabelColor = Color.Transparent,
                        focusedIndicatorColor =  Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent),
                )
                TextField(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    value = city,
                    textStyle = MaterialTheme.typography.labelSmall,
                    singleLine = true,
                    label = { Text("Grad",
                        color = colorResource(R.color.gray),
                        style = MaterialTheme.typography.bodySmall) },
                    supportingText = {
                        /*if (isError) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = "Opis može biti najviše 50 znakova.",
                                color = Color.Red
                            )
                        }*/
                    },
                    onValueChange = {
                        city = it
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        unfocusedTextColor = Color(android.graphics.Color.parseColor("#333333")),
                        containerColor  = Color.White,
                        cursorColor = Color(android.graphics.Color.parseColor("#52B788")),
                        disabledLabelColor = Color.Transparent,
                        focusedIndicatorColor =  Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent),
                )
                TextField(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    value = zipCode,
                    textStyle = MaterialTheme.typography.labelSmall,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    ),
                    label = { Text("Poštanski broj",
                        color = colorResource(R.color.gray),
                        style = MaterialTheme.typography.bodySmall
                    ) },
                    supportingText = {
                        /*if (isError) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = "Opis može biti najviše 50 znakova.",
                                color = Color.Red
                            )
                        }*/
                    },
                    onValueChange = { newValue ->
                        if (newValue.all { it.isDigit() } && newValue.length <= 5) {
                            zipCode = newValue
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        unfocusedTextColor = Color(android.graphics.Color.parseColor("#333333")),
                        containerColor  = Color.White,
                        cursorColor = Color(android.graphics.Color.parseColor("#52B788")),
                        disabledLabelColor = Color.Transparent,
                        focusedIndicatorColor =  Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent),
                )
            Spacer(modifier = Modifier.size(AppTheme.dimens.mediumLarge))
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
                    profileViewModel?.becomeWalker(userProfile = UserProfile(walker = Walker(
                        approved = false,
                        phoneNumber = phoneNumber,
                        address = address,
                        averageRating = 0.0,))
                    )
                    onApplied(false)
                })
            {
                Text(
                    modifier = Modifier,
                    text = "Pošalji",
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