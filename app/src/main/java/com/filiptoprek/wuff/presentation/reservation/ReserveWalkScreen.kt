package com.filiptoprek.wuff.presentation.reservation

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.filiptoprek.wuff.presentation.home.AppTitle
import com.filiptoprek.wuff.presentation.shared.SharedViewModel
import com.filiptoprek.wuff.ui.theme.AppTheme
import com.filiptoprek.wuff.ui.theme.Opensans
import com.filiptoprek.wuff.ui.theme.Pattaya

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReserveWalkScreen(reservationViewModel: ReservationViewModel, sharedViewModel: SharedViewModel, navController: NavHostController)
{
    val walkTypeList = reservationViewModel.walkTypeList.collectAsState()
    val reservationFlow = reservationViewModel.reservationCreateFlow.collectAsState()
    var selectedText = remember {
        mutableStateOf("Odaberite vrstu šetnje")
    }

    var dateString = remember {
        mutableStateOf("")
    }

    var timeString = remember {
        mutableStateOf("")
    }

    var isError by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .wrapContentHeight(Alignment.Top)
    ) {
        AppTitle()
        Spacer(modifier = Modifier.size(AppTheme.dimens.mediumLarge))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth()
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
                    text = "Rezerviraj šetnju",
                    style = TextStyle(
                        fontFamily = Opensans,
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    ),
                    color = colorResource(R.color.gray)

                )
                Spacer(modifier = Modifier.size(AppTheme.dimens.medium))
                AsyncImage(
                    modifier = Modifier
                        .clip(RoundedCornerShape(90.dp))
                        .border(
                            1.dp,
                            colorResource(R.color.gray),
                            shape = RoundedCornerShape(90.dp)
                        )
                        .size(AppTheme.dimens.smallImage),
                    model = sharedViewModel.selectedWalker?.user?.profilePhotoUrl,
                    placeholder = painterResource(id = R.drawable.user_placeholder),
                    error = painterResource(id = R.drawable.user_placeholder),
                    contentDescription = "User image",
                )
                Spacer(modifier = Modifier.size(AppTheme.dimens.smallMedium))
                Text(
                    text = sharedViewModel.selectedWalker?.user?.name.toString(),
                    style = TextStyle(
                        fontFamily = Opensans,
                        fontSize = MaterialTheme.typography.bodySmall.fontSize,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = colorResource(R.color.gray)
                    )
                )
                Spacer(modifier = Modifier.size(AppTheme.dimens.small))
                if (isError){
                    Text(
                        text = errorText,
                        style = TextStyle(
                            fontFamily = Opensans,
                            fontSize = MaterialTheme.typography.bodySmall.fontSize,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = Color.Red
                        )
                    )
                }
                DropDownMenu(selectedText, walkTypeList.value)

                DateTimePickers(dateString, timeString)

                Spacer(modifier = Modifier.size(AppTheme.dimens.smallMedium))
                Text(
                    text = if (selectedText.value != "Odaberite vrstu šetnje") {
                        "${walkTypeList.value.find { it?.walkName == selectedText.value }?.walkPrice}€"
                    } else {
                        "Cijena nije dostupna"
                    },
                    style = TextStyle(
                        fontFamily = Opensans,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                )
                Spacer(modifier = Modifier.size(10.dp))

                reservationFlow.value?.let {
                    when(it){
                        is Resource.Failure -> {
                            isError = true
                            errorText = it.exception.message.toString()
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
                            navController.popBackStack()
                            navController.navigate(Routes.Home.route)
                        }
                    }
                }
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
                        isError = false
                        if(selectedText.value == "Odaberite vrstu šetnje")
                        {
                            isError = true
                            errorText = "Molimo odaberite vrstu šetnje"
                            return@Button
                        }

                        reservationViewModel.createReservation(
                            Reservation("", sharedViewModel.selectedWalker?.user?.uid.toString(),"", dateString.value
                                , timeString.value,false,false, false, false, walkTypeList.value.find { it?.walkName == selectedText.value }?.walkPrice!!,
                                walkType = walkTypeList.value.find { type -> type?.walkName == selectedText.value }!!
                            )
                        )
                    })
                {
                    Text(
                        modifier = Modifier,
                        text = "Rezerviraj",
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