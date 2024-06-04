package com.filiptoprek.wuff.presentation.reservation

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.filiptoprek.wuff.R
import com.filiptoprek.wuff.data.repository.location.LocationClientImpl
import com.filiptoprek.wuff.domain.model.reservation.Reservation
import com.filiptoprek.wuff.navigation.Routes
import com.filiptoprek.wuff.presentation.auth.AuthViewModel
import com.filiptoprek.wuff.presentation.location.LocationViewModel
import com.filiptoprek.wuff.presentation.shared.SharedViewModel
import com.filiptoprek.wuff.service.LocationService
import com.filiptoprek.wuff.ui.theme.Opensans
import com.filiptoprek.wuff.ui.theme.Pattaya

@OptIn(ExperimentalTextApi::class)
@Composable
fun ReservationDetailsScreen(
    reservation: Reservation,
    reservationViewModel: ReservationViewModel,
    navController: NavHostController,
    authViewModel: AuthViewModel,
    locationViewModel: LocationViewModel,
    sharedViewModel: SharedViewModel
)
{
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth()
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
                Text(
                    text = "Detalji rezervacije",
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
                    model = if (authViewModel.currentUser?.uid == reservation.walkerUserId) {
                        reservation.user?.user?.profilePhotoUrl
                    } else {
                        reservation.walker?.user?.profilePhotoUrl
                    },
                    placeholder = painterResource(id = R.drawable.user_placeholder),
                    error = painterResource(id = R.drawable.user_placeholder),
                    contentDescription = "User image",
                )
                Spacer(modifier = Modifier.size(15.dp))
                val fontFamily = FontFamily(Font(
                    R.font.opensans_variable, variationSettings = FontVariation.Settings(
                        FontVariation.weight(100)
                    )
                ))
                Text(
                    text = if (authViewModel.currentUser?.uid == reservation.walkerUserId) {
                        reservation.user?.user?.name.toString()
                    } else {
                        reservation.walker?.user?.name.toString()
                    },
                    style = TextStyle(
                        fontFamily = Opensans,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = colorResource(R.color.background_dark)
                    )
                )
                Spacer(modifier = Modifier.size(20.dp))
                Text(
                    text = "Tip šetnje",
                    style = TextStyle(
                        fontFamily = Opensans,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        color = colorResource(R.color.gray)
                    )
                )
                Text(
                    text = reservation.walkType.walkName,
                    style = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Thin,
                        textAlign = TextAlign.Center,
                        color = colorResource(R.color.gray)
                    )
                )
                Spacer(modifier = Modifier.size(10.dp))
                Text(
                    text = "Datum šetnje",
                    style = TextStyle(
                        fontFamily = Opensans,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        color = colorResource(R.color.gray)
                    )
                )
                Text(
                    text = "${reservation.dateOfWalk}",
                    style = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Thin,
                        textAlign = TextAlign.Center,
                        color = colorResource(R.color.gray)
                    )
                )
                Spacer(modifier = Modifier.size(10.dp))
                Text(
                    text = "Vrijeme šetnje",
                    style = TextStyle(
                        fontFamily = Opensans,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        color = colorResource(R.color.gray)
                    )
                )
                Text(
                    text = "${reservation.timeOfWalk}",
                    style = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Thin,
                        textAlign = TextAlign.Center,
                        color = colorResource(R.color.gray)
                    )
                )
                Spacer(modifier = Modifier.size(10.dp))
                Text(
                    text = "Cijena šetnje",
                    style = TextStyle(
                        fontFamily = Opensans,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        color = colorResource(R.color.gray)
                    )
                )
                Text(
                    text = "${reservation.price} €",
                    style = TextStyle(
                        fontFamily = Opensans,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = colorResource(R.color.gray)
                    )
                )
                Spacer(modifier = Modifier.size(10.dp))
                when {
                    reservation.declined && authViewModel.currentUser?.uid == reservation.userId ->
                        ReservationText("Rezervacija je odbijena", Color.Red)

                    reservation.started && !reservation.completed && authViewModel.currentUser?.uid == reservation.userId -> {
                        ReservationText("Šetnja u tijeku", colorResource(R.color.green_accent))
                        if(authViewModel.currentUser?.uid != reservation.walkerUserId)
                        {
                            ActionButton("Pratite šetača", colorResource(R.color.green_accent)) {
                                locationViewModel.getWalkerLocation(sharedViewModel.selectedReservation?.walker!!)
                                navController.navigate(Routes.TrackLocation.route)
                            }
                        }
                    }


                    !reservation.completed ->
                        ReservationText(
                            if (reservation.accepted) "Rezervacija je prihvaćena" else "Rezervacija nije još prihvaćena",
                            if (reservation.accepted) colorResource(R.color.green_accent) else Color.Red
                        )

                    reservation.completed && authViewModel.currentUser?.uid == reservation.userId -> {
                        ReservationText("Šetnja je završena", colorResource(R.color.green_accent))
                        if (!reservation.rated) {
                            ActionButton("Ocijenite šetnju", colorResource(R.color.green_accent)) {
                                sharedViewModel.reservationToRate = reservation
                                navController.navigate(Routes.RateWalker.route)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.size(20.dp))

                val context = LocalContext.current

                if (authViewModel.currentUser?.uid == reservation.walkerUserId) {
                    when {
                        !reservation.accepted ->
                            ActionButton("Prihvati šetnju", colorResource(R.color.green_accent)) {
                                reservationViewModel.acceptReservation(reservation)
                                navController.popBackStack()
                            }

                        reservation.accepted && !reservation.completed && !reservation.started ->
                            ActionButton("Započni šetnju", colorResource(R.color.green_accent)) {
                                reservationViewModel.startWalk(reservation)
                                Intent(context, LocationService::class.java).apply {
                                    action = LocationService.ACTION_START
                                    context.startService(this)
                                }
                                navController.popBackStack()
                            }

                        reservation.started && !reservation.completed ->
                            ActionButton("Završi šetnju", Color.Red) {
                                reservationViewModel.endWalk(reservation)
                                Intent(context, LocationService::class.java).apply {
                                    action = LocationService.ACTION_STOP
                                    context.startService(this)
                                }
                                navController.popBackStack()
                            }
                    }
                }

                if (!reservation.accepted) {
                    ActionButton(
                        text = if (authViewModel.currentUser?.uid == reservation.walkerUserId) "Odbij šetnju" else if (reservation.declined) "Obriši šetnju" else "Otkaži šetnju",
                        color = Color.Red
                    ) {
                        if (authViewModel.currentUser?.uid == reservation.walkerUserId) {
                            reservationViewModel.declineReservation(reservation)
                        } else {
                            reservationViewModel.deleteReservation(reservation)
                        }
                        navController.popBackStack()
                    }
                }
                ActionButton("Natrag", colorResource(R.color.green_accent)) {
                    navController.popBackStack()
                }
            }
        }
    }
}