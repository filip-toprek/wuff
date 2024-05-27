package com.filiptoprek.wuff.presentation.reservation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.filiptoprek.wuff.R
import com.filiptoprek.wuff.domain.model.auth.Resource
import com.filiptoprek.wuff.domain.model.reservation.Reservation
import com.filiptoprek.wuff.presentation.auth.AuthViewModel
import com.filiptoprek.wuff.presentation.profile.ProfileViewModel
import com.filiptoprek.wuff.presentation.rating.RatingViewModel
import com.filiptoprek.wuff.ui.theme.Opensans
import com.filiptoprek.wuff.ui.theme.Pattaya
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.delay

@Composable
fun ReservationsScreen(
    navController: NavHostController = rememberNavController(),
    reservationViewModel: ReservationViewModel,
    profileViewModel: ProfileViewModel,
    ratingViewModel: RatingViewModel,
    authViewModel: AuthViewModel
) {
    val reservationFlow = reservationViewModel.reservationFlow.collectAsState()
    val reservationList = reservationViewModel.reservationsList.collectAsState()

    var isInDetails by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    reservationFlow.value?.let {
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
        Spacer(modifier = Modifier.size(20.dp))
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 40.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()

                ) {
                    if (!isInDetails){
                        Text(
                            text = "Rezervacije",
                            color = colorResource(R.color.gray),
                            style = TextStyle(
                                fontFamily = Opensans,
                                fontSize = 18.sp,
                                lineHeight = 27.sp,
                                textAlign = TextAlign.Start
                            )
                        )
                        Spacer(Modifier.weight(1f))
                    }
                }
                if (isLoading) {
                    profileViewModel.refreshUser()
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally)
                            .wrapContentHeight(Alignment.CenterVertically),
                        color = colorResource(R.color.green_accent)
                    )
                } else {
                    val onDetailsChange: (Boolean) -> Unit = { newValue ->
                        isInDetails = newValue
                    }
                    reservationCard(reservationList.value, onDetailsChange, reservationViewModel, reservationFlow, profileViewModel.userProfile?.walker?.approved, ratingViewModel, authViewModel)
                }
            }
        }
    }
}
@Composable
fun reservationCard(
    reservationList: List<Reservation?>,
    onDetailsChange: (Boolean) -> Unit,
    reservationViewModel: ReservationViewModel,
    reservationFlow: State<Resource<Any>?>,
    approvedWalker: Boolean?,
    ratingViewModel: RatingViewModel,
    authViewModel: AuthViewModel
)
{
    var isDetailsClicked by remember { mutableStateOf(false) }
    var selectedReservation by remember { mutableStateOf(Reservation()) }
    onDetailsChange(isDetailsClicked)
    val onBackClick: (Boolean) -> Unit = { newValue ->
        isDetailsClicked = newValue
    }
    if(isDetailsClicked)
    {
        reservationDetails(selectedReservation, onBackClick, reservationViewModel, reservationFlow, approvedWalker, ratingViewModel, authViewModel)
    }else {
        if(reservationList.isEmpty())
        {
            Column(
                modifier = Modifier
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .wrapContentHeight(Alignment.Top),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.size(20.dp))
                Text(
                    text = "Trenutno nemate rezervacija.",
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
        var refreshing by remember { mutableStateOf(false) }
        LaunchedEffect(refreshing) {
            if (refreshing) {
                reservationViewModel.refreshReservations()
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

                items(reservationList) { reservation ->
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
                                    .height(40.dp),
                                model = if (approvedWalker == true) {
                                    reservation?.user?.user?.profilePhotoUrl
                                } else {
                                    reservation?.walker?.user?.profilePhotoUrl
                                },
                                placeholder = painterResource(id = R.drawable.user_placeholder),
                                error = painterResource(id = R.drawable.user_placeholder),
                                contentDescription = "User image",
                            )
                            Spacer(modifier = Modifier.size(35.dp))
                            Column {
                                Text(
                                    text = if (approvedWalker == true) {
                                        reservation?.user?.user?.name.toString()
                                    } else {
                                        reservation?.walker?.user?.name.toString()
                                    },
                                    color = colorResource(R.color.gray),
                                    style = TextStyle(
                                        fontFamily = Opensans,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        color = Color.White
                                    )
                                )
                                Text(
                                    text = "${reservation?.price.toString()}€",
                                    color = colorResource(R.color.gray),
                                    style = TextStyle(
                                        fontFamily = Opensans,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Normal,
                                        textAlign = TextAlign.Center,
                                        color = colorResource(R.color.box_bkg_white)
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.weight(1f))
                            Button(modifier = Modifier.size(width = 115.dp, height = 40.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.green_accent)
                                ),
                                contentPadding = PaddingValues(0.dp),
                                onClick = {
                                    selectedReservation = reservation!!
                                    isDetailsClicked = true
                                })
                            {
                                Text(
                                    modifier = Modifier,
                                    text = "Detalji",
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
    }


@Composable
fun reservationDetails(
    reservation: Reservation,
    onBackClick: (Boolean) -> Unit,
    reservationViewModel: ReservationViewModel,
    reservationFlow: State<Resource<Any>?>,
    approvedWalker: Boolean?,
    ratingViewModel: RatingViewModel,
    authViewModel: AuthViewModel
)
{
    var isRating by remember {
        mutableStateOf(false)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth()
            .padding(15.dp)
            .background(colorResource(R.color.box_bkg_white), RoundedCornerShape(8.dp))
            .padding(15.dp)
            .height(IntrinsicSize.Min)
    ) {
        Column(
            modifier = Modifier
                .wrapContentWidth(Alignment.CenterHorizontally)
                .wrapContentHeight(Alignment.Top),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if(isRating)
            {
                val onRating: (Boolean) -> Unit = { newValue ->
                    isRating = newValue
                }
                rateWalker(ratingViewModel, authViewModel, reservation.walker!!, reservation.reservationId, onRating)
            }else
            {
                Text(
                    text = "Rezervacija",
                    style = TextStyle(
                        fontFamily = Opensans,
                        fontSize = 23.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    ),
                    color = colorResource(R.color.gray)

                )
                Spacer(modifier = Modifier.size(20.dp))
                AsyncImage(
                    modifier = Modifier
                        .clip(RoundedCornerShape(90.dp))
                        .border(
                            1.dp,
                            colorResource(R.color.gray),
                            shape = RoundedCornerShape(90.dp)
                        )
                        .size(100.dp),
                    model = if(approvedWalker == true)
                    {
                        reservation.user?.user?.profilePhotoUrl
                    }else
                    {
                        reservation.walker?.user?.profilePhotoUrl
                    },
                    placeholder = painterResource(id = R.drawable.user_placeholder),
                    error = painterResource(id = R.drawable.user_placeholder),
                    contentDescription = "User image",
                )
                Spacer(modifier = Modifier.size(15.dp))
                Text(
                    text = if(approvedWalker == true)
                    {
                        reservation.user?.user?.name.toString()
                    }else
                    {
                        reservation.walker?.user?.name.toString()
                    },
                    style = TextStyle(
                        fontFamily = Opensans,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = colorResource(R.color.gray)
                    )
                )
                Spacer(modifier = Modifier.size(10.dp))

                Text(
                    text = reservation.walkType.walkName,
                    style = TextStyle(
                        fontFamily = Opensans,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                )
                Spacer(modifier = Modifier.size(10.dp))
                Text(
                    text = "${reservation.dateOfWalk}",
                    style = TextStyle(
                        fontFamily = Opensans,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                )
                Spacer(modifier = Modifier.size(10.dp))
                Text(
                    text = "${reservation.timeOfWalk}",
                    style = TextStyle(
                        fontFamily = Opensans,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                )
                Spacer(modifier = Modifier.size(10.dp))
                Text(
                    text = "${reservation.price} €",
                    style = TextStyle(
                        fontFamily = Opensans,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                )
                Spacer(modifier = Modifier.size(10.dp))
                if(reservation.declined && approvedWalker == null)
                {
                    Text(
                        text = "Rezervacija je odbijena",
                        style = TextStyle(
                            fontFamily = Opensans,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = if(reservation.accepted) colorResource(R.color.green_accent) else Color.Red
                        )
                    )
                }else if(!reservation.completed)
                {
                    Text(
                        text = if(reservation.accepted) "Rezervacija je prihvaćena" else "Rezervacija nije još prihvaćena",
                        style = TextStyle(
                            fontFamily = Opensans,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = if(reservation.accepted) colorResource(R.color.green_accent) else Color.Red
                        )
                    )
                }else if (reservation.completed && !reservation.rated && approvedWalker == null)
                {
                    Text(
                        text = "Šetnja je završena",
                        style = TextStyle(
                            fontFamily = Opensans,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = colorResource(R.color.green_accent)
                        )
                    )

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
                            isRating = true
                        })
                    {
                        Text(
                            modifier = Modifier,
                            text = "Ocijenite šetnju",
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
                Spacer(modifier = Modifier.size(10.dp))
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
                        onBackClick(false)
                    })
                {
                    Text(
                        modifier = Modifier,
                        text = "Natrag",
                        style = TextStyle(
                            fontFamily = Opensans,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = Color.White
                        )
                    )
                }
                if(approvedWalker == true && !reservation.accepted)
                {
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
                            reservationViewModel.acceptReservation(reservation)
                            onBackClick(false)
                        })
                    {
                        Text(
                            modifier = Modifier,
                            text = "Prihvati šetnju",
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
                reservationFlow.value?.let {
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
                        }
                    }
                }
                if(approvedWalker == true && reservation.accepted && !reservation.completed && !reservation.started)
                {
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
                            reservationViewModel.startWalk(reservation)
                            onBackClick(false)
                        })
                    {
                        Text(
                            modifier = Modifier,
                            text = "Započni šetnju",
                            style = TextStyle(
                                fontFamily = Opensans,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                color = Color.White
                            )
                        )
                    }
                }else if(approvedWalker == true && reservation.started && reservation.accepted && !reservation.completed)
                {
                    Button(modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                        .wrapContentHeight(Alignment.CenterVertically)
                        .width(IntrinsicSize.Max),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red
                        ),
                        onClick = {
                            reservationViewModel.endWalk(reservation)
                            onBackClick(false)
                        })
                    {
                        Text(
                            modifier = Modifier,
                            text = "Završi šetnju",
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
                if(!reservation.accepted) {
                    Button(modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                        .wrapContentHeight(Alignment.CenterVertically)
                        .width(IntrinsicSize.Max),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red
                        ),
                        onClick = {
                            if(approvedWalker == true)
                            {
                                reservationViewModel.declineReservation(reservation)
                            }else{
                                reservationViewModel.deleteReservation(reservation)
                            }
                            onBackClick(false)
                        })
                    {
                        Text(
                            modifier = Modifier,
                            text = if(approvedWalker == true)
                            {
                                "Odbij šetnju"
                            }else
                            {
                                if(reservation.declined)
                                {
                                    "Obriši šetnju"
                                }else
                                {
                                    "Otkaži šetnju"
                                }
                            },
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