package com.filiptoprek.wuff.presentation.withdraw

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.substring
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import arrow.fx.coroutines.unit
import com.filiptoprek.wuff.R
import com.filiptoprek.wuff.domain.model.auth.Resource
import com.filiptoprek.wuff.domain.model.reservation.Reservation
import com.filiptoprek.wuff.domain.model.withdraw.Withdraw
import com.filiptoprek.wuff.navigation.Routes
import com.filiptoprek.wuff.presentation.profile.ProfileViewModel
import com.filiptoprek.wuff.presentation.reservation.ReservationViewModel
import com.filiptoprek.wuff.presentation.reservation.reservationCard
import com.filiptoprek.wuff.presentation.shared.SharedViewModel
import com.filiptoprek.wuff.ui.theme.Opensans
import com.filiptoprek.wuff.ui.theme.Pattaya
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.firebase.Timestamp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

@Composable
fun WithdrawalScreen(
    navController: NavHostController,
    withdrawViewModel: WithdrawViewModel,
) {
    val withdrawFlow = withdrawViewModel.withdrawFlow.collectAsState()
    val withdrawals = withdrawViewModel.withdrawList.collectAsState()

    LaunchedEffect(Unit) {
        withdrawViewModel.refresh()
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
                        text = "Isplate",
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
                withdrawFlow.value?.let {
                    when(it){
                        is Resource.Failure -> {
                            navController.popBackStack()
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
                            withdrawalCard(navController, withdrawals.value?.withdrawals!!, withdrawViewModel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun withdrawalCard(
    navController: NavHostController,
    withdrawalsList: List<Withdraw?>,
    withdrawViewModel: WithdrawViewModel,
)
{
    var refreshing by remember { mutableStateOf(false) }

    LaunchedEffect(refreshing) {
        if (refreshing) {
            withdrawViewModel.refresh()
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
            items(1){
                Button(modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.green_accent)
                    ),
                    onClick = {
                        navController.navigate(Routes.CreateWithdraw.route)
                    })
                {
                    Text(
                        modifier = Modifier,
                        text = "Nova isplata",
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
            items(withdrawalsList) { withdraw ->
                if(withdrawalsList.isEmpty())
                {
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
                            text = "Trenutno nemate zatraženih isplata.",
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
                Row(
                    modifier = Modifier
                        .padding(top = 15.dp)
                        .wrapContentWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,

                    ) {
                    Text(
                        text = "Vrijednost: "+withdraw?.amount.toString(),
                        color = colorResource(R.color.gray),
                        style = TextStyle(
                            fontFamily = Opensans,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.size(20.dp))
                    Text(
                        text = "status: "+ if(!withdraw?.completed!!) "U tijeku" else "Isplaćeno",
                        color = if(!withdraw.completed) colorResource(R.color.gray) else colorResource(R.color.green_accent),
                        style = TextStyle(
                            fontFamily = Opensans,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                        )
                    )
                    fun formatTimestamp(timestamp: Timestamp): String {
                        val date = timestamp.toDate()
                        val formatter = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
                        return formatter.format(date)
                    }
                    Spacer(modifier = Modifier.size(20.dp))
                    Text(
                        text = formatTimestamp(withdraw.dateCreated),
                        color = colorResource(R.color.gray),
                        style = TextStyle(
                            fontFamily = Opensans,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = colorResource(R.color.gray)
                        )
                    )
                }
            }
        }
    }
}