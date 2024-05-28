package com.filiptoprek.wuff.presentation.reservation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import coil.compose.AsyncImage
import com.filiptoprek.wuff.R
import com.filiptoprek.wuff.domain.model.auth.Resource
import com.filiptoprek.wuff.domain.model.reservation.Reservation
import com.filiptoprek.wuff.navigation.Routes
import com.filiptoprek.wuff.presentation.profile.ProfileViewModel
import com.filiptoprek.wuff.presentation.shared.SharedViewModel
import com.filiptoprek.wuff.ui.theme.Opensans
import com.filiptoprek.wuff.ui.theme.Pattaya
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ReservationsScreen(
    navController: NavHostController,
    reservationViewModel: ReservationViewModel,
    profileViewModel: ProfileViewModel,
    sharedViewModel: SharedViewModel
) {
    val reservationFlow = reservationViewModel.reservationFlow.collectAsState()
    val reservationList = reservationViewModel.reservationsList.collectAsState()
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
                    reservationCard(navController ,reservationList.value, reservationViewModel, sharedViewModel, profileViewModel)
                }
            }
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun reservationCard(
    navController: NavHostController,
    reservationList: List<Reservation?>,
    reservationViewModel: ReservationViewModel,
    sharedViewModel: SharedViewModel,
    profileViewModel: ProfileViewModel
)
{
    var refreshing by remember { mutableStateOf(false) }
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
        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        val sortedReservationList = reservationList.sortedBy { reservation ->
            LocalDate.parse(reservation?.dateOfWalk, dateFormatter)
        }
        LazyColumn(
            modifier = Modifier.fillMaxHeight()
        ) {
            items(sortedReservationList) { reservation ->
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
                                    sharedViewModel.userProfile = if(profileViewModel.userProfile?.user?.uid == reservation?.walkerUserId) reservation?.user!! else reservation?.walker!!
                                    navController.navigate(Routes.userProfile.route)
                                },
                            model = if (profileViewModel.userProfile?.user?.uid == reservation?.walkerUserId) {
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
                                text = if (profileViewModel.userProfile?.user?.uid == reservation?.walkerUserId) {
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
                            when{
                                    reservation?.declined!! ->
                                        ReservationStatusText("Šetnja odbijena", Color.Red)

                                    reservation.started && !reservation.completed ->
                                        ReservationStatusText("Šetnja u tijeku", colorResource(R.color.green_accent))

                                    !reservation.completed ->
                                        ReservationStatusText(
                                            if (reservation.accepted) "Šetnja prihvaćena" else "Čeka potvrdu",
                                            if (reservation.accepted) colorResource(R.color.green_accent) else Color.Red
                                        )

                                    reservation.completed -> {
                                        ReservationStatusText("Šetnja završena", colorResource(R.color.green_accent))
                                    }
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))
                        Button(modifier = Modifier.size(width = 115.dp, height = 40.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorResource(R.color.green_accent)
                            ),
                            contentPadding = PaddingValues(0.dp),
                            onClick = {
                                sharedViewModel.selectedReservation = reservation
                                navController.navigate(Routes.reservationDetails.route)
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




@Composable
fun ReservationStatusText(text: String, color: Color) {
    Text(
        text = text,
        style = TextStyle(
            fontFamily = Opensans,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = color
        )
    )
}

@Composable
fun ReservationText(text: String, color: Color) {
    Text(
        text = text,
        style = TextStyle(
            fontFamily = Opensans,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = color
        )
    )
}

@Composable
fun ActionButton(text: String, color: Color, onClick: () -> Unit) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .wrapContentHeight(Alignment.CenterVertically)
            .width(IntrinsicSize.Max),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        onClick = onClick
    ) {
        Text(
            modifier = Modifier,
            text = text,
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