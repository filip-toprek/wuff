package com.filiptoprek.wuff.presentation.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.filiptoprek.wuff.R
import com.filiptoprek.wuff.domain.model.auth.Resource
import com.filiptoprek.wuff.domain.model.profile.UserProfile
import com.filiptoprek.wuff.navigation.Routes
import com.filiptoprek.wuff.presentation.location.LocationViewModel
import com.filiptoprek.wuff.presentation.profile.ProfileViewModel
import com.filiptoprek.wuff.presentation.reload.ReloadViewModel
import com.filiptoprek.wuff.presentation.reservation.ReservationViewModel
import com.filiptoprek.wuff.presentation.shared.SharedViewModel
import com.filiptoprek.wuff.ui.theme.Opensans
import com.filiptoprek.wuff.ui.theme.Pattaya
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.delay

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    navController: NavHostController,
    homeViewModel: HomeViewModel,
    profileViewModel: ProfileViewModel,
    sharedViewModel: SharedViewModel,
    locationViewModel: LocationViewModel
    ){
    val homeFlow = homeViewModel.homeFlow.collectAsState()
    val walkerList = homeViewModel.walkerList.collectAsState()

    var isLoading by remember { mutableStateOf(false) }
    var reserved by remember { mutableStateOf(false) }

    homeFlow.value?.let {
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

    LaunchedEffect(Unit) {
        locationViewModel.getLocationOnStart()
    }

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
        if(reserved)
        {
            homeFlow.value?.let {
                when(it){
                    is Resource.Failure -> {
                    }
                    Resource.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentWidth(Alignment.CenterHorizontally)
                                .wrapContentHeight(Alignment.CenterVertically),
                            color = colorResource(R.color.green_accent)
                        )
                    }
                    is Resource.Success -> {
                        navController.navigate(Routes.ResereveWalk.route)
                    }
                }
            }
        }else{
            Column(
                modifier = Modifier.padding(horizontal = 40.dp)
            ) {
                Spacer(modifier = Modifier.size(20.dp))

                if(profileViewModel.userProfile?.walker?.approved == true)
                {
                    Text(
                        text = "Pozdrav, ${profileViewModel.userProfile?.user?.name.toString().split(" ")[0]}",
                        color = colorResource(R.color.gray),
                        style = TextStyle(
                            fontFamily = Opensans,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center,
                            color = Color.White
                        )
                    )
                    walkerStats(profileViewModel)

                    Button(modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.green_accent)
                        ),
                        onClick = {
                            navController.navigate(Routes.Withdrawals.route)
                        })
                    {
                        Text(
                            modifier = Modifier,
                            text = "Isplati",
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
                    infoCard("Jednostavno i brzno nadopuni svoj novčanik", "Nadopuni", navController)
                    Spacer(modifier = Modifier.size(20.dp))
                    Column(
                        modifier = Modifier
                    ) {
                        Row (
                            modifier = Modifier
                                .fillMaxWidth()

                        ){
                            Text(
                                text = "Šetači",
                                style = TextStyle(
                                fontFamily = Opensans,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                color = colorResource(R.color.gray))
                            )
                            Spacer(Modifier.weight(1f))
                        }
                        if(isLoading)
                        {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentWidth(Alignment.CenterHorizontally)
                                    .wrapContentHeight(Alignment.CenterVertically),
                                color = colorResource(R.color.green_accent)
                            )
                        }else {
                            walkerTab(walkerList.value, homeViewModel, navController, sharedViewModel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun infoCard(text: String, buttonText: String, navController: NavHostController)
{
    Row(
        modifier = Modifier
            .width(IntrinsicSize.Max)
            .fillMaxWidth()
            .wrapContentWidth()
            .background(colorResource(R.color.background_dark), RoundedCornerShape(8.dp))
            .padding(15.dp),

    ){
        Column(
            modifier = Modifier
                .widthIn(min = 155.dp, max = 180.dp)
                .wrapContentSize(),
        )
        {
            Text(
                text = text,
                modifier = Modifier.padding(bottom = 15.dp),
                color = Color.White
            )
            Button(modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.Start),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.green_accent)
                ),
                onClick = {
                    navController.navigate(Routes.Reload.route)

                })
            {
                Text(
                    modifier = Modifier,
                    text = buttonText,
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
        Column(
            modifier = Modifier.wrapContentSize()
        ) {
            Image(
                painterResource(R.drawable.dog), "dog image",
                modifier = Modifier
                    .size(100.dp)
                    .aspectRatio(1f),
                contentScale = ContentScale.FillHeight
            )
        }
    }
}


@Composable
fun walkerTab(walkerList: List<UserProfile?>, homeViewModel: HomeViewModel, navController: NavHostController, sharedViewModel: SharedViewModel)
{
    var refreshing by remember { mutableStateOf(false) }

    LaunchedEffect(refreshing) {
        if (refreshing) {
            homeViewModel.refresh()
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
            if(walkerList.isEmpty())
            {
                items(1){
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally)
                            .wrapContentHeight(Alignment.Top),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Spacer(modifier = Modifier.size(20.dp))
                        Text(
                            text = "Trenutno nema šetača u vašem mjestu.",
                            style = TextStyle(
                                fontFamily = Opensans,
                                fontSize = 13.sp,
                                lineHeight = 20.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                            ),
                            color = colorResource(R.color.gray)
                        )
                    }
                }
            }
            items(walkerList) { walker ->
                Row(
                    modifier = Modifier
                        .padding(top = 15.dp)
                        .wrapContentWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,

                    ) {
                    AsyncImage(
                        modifier = Modifier
                            .clip(RoundedCornerShape(90.dp))
                            .height(40.dp)
                            .clickable {
                                sharedViewModel.userProfile = walker
                                navController.navigate(Routes.UserProfile.route)
                            },
                        model = walker?.user?.profilePhotoUrl,
                        placeholder = painterResource(id = R.drawable.user_placeholder),
                        error = painterResource(id = R.drawable.user_placeholder),
                        contentDescription = "User image",
                    )
                    Spacer(modifier = Modifier.size(35.dp))
                    Text(
                        text = walker?.user?.name.toString(),
                        color = colorResource(R.color.gray),
                        style = TextStyle(
                            fontFamily = Opensans,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Button(modifier = Modifier.size(width = 115.dp, height = 40.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.green_accent)
                        ),
                        contentPadding = PaddingValues(0.dp),
                        onClick = {
                            sharedViewModel.selectedWalker = walker!!
                            navController.navigate(Routes.ResereveWalk.route)
                        })
                    {
                        Text(
                            modifier = Modifier,
                            text = "Rezerviraj",
                            style = TextStyle(
                                fontFamily = Opensans,
                                fontSize = 14.sp,
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
fun walkerStats(profileViewModel: ProfileViewModel)
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
                text = "Ocjena",
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
                text = "${profileViewModel.userProfile?.walker?.averageRating}/5.0",
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