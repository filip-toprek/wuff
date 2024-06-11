package com.filiptoprek.wuff.presentation.withdraw

import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.filiptoprek.wuff.R
import com.filiptoprek.wuff.domain.model.auth.Resource
import com.filiptoprek.wuff.domain.model.reload.Reload
import com.filiptoprek.wuff.domain.model.withdraw.Withdraw
import com.filiptoprek.wuff.domain.model.withdraw.WithdrawProfile
import com.filiptoprek.wuff.navigation.Routes
import com.filiptoprek.wuff.ui.theme.Opensans
import com.filiptoprek.wuff.ui.theme.Pattaya
import com.google.firebase.Timestamp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WithdrawRequestScreen(
    navController: NavHostController,
    withdrawViewModel: WithdrawViewModel,
) {
    val withdrawFlow = withdrawViewModel.createWithdrawFlow.collectAsState()
    val withdrawals = withdrawViewModel.withdrawList.collectAsState()
    var iban by remember { mutableStateOf(withdrawals.value?.withdrawProfile?.iban.toString()) }
    var swift by remember { mutableStateOf(withdrawals.value?.withdrawProfile?.swift.toString()) }
    var withdrawAmount by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf("") }

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
                Text(
                    text = "Nova isplata",
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
                Text(
                    text = "IBAN",
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
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    value = iban,
                    textStyle = TextStyle(
                        fontFamily = Opensans,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Start,
                        color = colorResource(R.color.gray),
                    ),
                    supportingText = {

                    },
                    onValueChange = { newValue ->
                        if (newValue.isNotEmpty() && newValue.length <= 21) {
                            iban = newValue
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        unfocusedTextColor = colorResource(R.color.gray),
                        containerColor = Color.White,
                        cursorColor = colorResource(R.color.green_accent),
                        disabledLabelColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                )
                Text(
                    text = "SWIFT",
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
                    value = swift,
                    textStyle = TextStyle(
                        fontFamily = Opensans,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Start,
                        color = colorResource(R.color.gray),
                    ),
                    supportingText = {

                    },
                    onValueChange = { newValue ->
                        if (newValue.length <= 11) {
                            swift = newValue
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        unfocusedTextColor = colorResource(R.color.gray),
                        containerColor = Color.White,
                        cursorColor = colorResource(R.color.green_accent),
                        disabledLabelColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                )
                Spacer(modifier = Modifier.size(10.dp))
                Text(
                    text = "Vrijednost isplate",
                    color = colorResource(R.color.gray),
                    style = TextStyle(
                        fontFamily = Opensans,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                )
                TextField(
                    modifier = Modifier.width(60.dp),
                    value = withdrawAmount,
                    textStyle = TextStyle(
                        fontFamily = Opensans,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Start,
                        color = colorResource(R.color.gray),
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    ),
                    supportingText = {

                    },
                    onValueChange = { newValue ->
                        if (newValue.length <= 2) {
                            withdrawAmount = newValue
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        unfocusedTextColor = colorResource(R.color.gray),
                        containerColor = Color.White,
                        cursorColor = colorResource(R.color.green_accent),
                        disabledLabelColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                )
                withdrawFlow.value?.let {
                    when (it) {
                        is Resource.Failure -> {
                            errorText = when(it.exception.message.toString())
                            {
                                "BAD_IBAN" -> "Loše unesen IBAN"
                                "BAD_AMOUNT" -> "Unesite vrijednost isplate"
                                else -> it.exception.message.toString()
                            }
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
                            navController.navigate(Routes.Withdrawals.route)
                        }
                    }
                }
                Text(
                    text = errorText,
                    style = TextStyle(
                        fontFamily = Opensans,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color.Red
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
                        errorText = ""
                        withdrawViewModel.createWithdrawal(Withdraw(try { withdrawAmount.toDouble()} catch(e: Exception) { Double.NaN },
                            Timestamp.now(), false), if(withdrawals.value?.withdrawProfile!! == WithdrawProfile()) WithdrawProfile(iban, swift) else withdrawals.value?.withdrawProfile!!)
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

            }
        }
    }
}