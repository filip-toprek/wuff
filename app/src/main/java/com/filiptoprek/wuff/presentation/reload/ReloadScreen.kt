package com.filiptoprek.wuff.presentation.reload

import android.app.Activity
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.json.responseJson
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.filiptoprek.wuff.R
import com.filiptoprek.wuff.domain.model.reload.Reload
import com.filiptoprek.wuff.domain.model.auth.Resource
import com.filiptoprek.wuff.navigation.Routes
import com.filiptoprek.wuff.presentation.home.AppTitle
import com.filiptoprek.wuff.ui.theme.Opensans
import com.filiptoprek.wuff.ui.theme.Pattaya
import com.github.kittinunf.result.Result
import com.stripe.android.PaymentConfiguration
import com.stripe.android.payments.paymentlauncher.PaymentLauncher
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.stripe.android.paymentsheet.rememberPaymentSheet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.http.Body

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReloadScreen(reloadViewModel: ReloadViewModel, navController: NavHostController) {
    var reloadAmount by remember { mutableStateOf("") }
    var reloadFlow = reloadViewModel.reloadFlow.collectAsState()
    val context = LocalContext.current as Activity
    val paymentSheet = rememberPaymentSheet(reloadViewModel::onPaymentSheetResult)
    var customerConfig by remember { mutableStateOf<PaymentSheet.CustomerConfiguration?>(null) }
    var paymentIntentClientSecret by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .background(colorResource(R.color.background_white))
            .fillMaxSize()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .wrapContentHeight(Alignment.Top)
    ) {
        AppTitle()
        Spacer(modifier = Modifier.size(20.dp))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TextField(
                modifier = Modifier.width(60.dp),
                value = reloadAmount.toString(),
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
                    if (newValue.all { it.isDigit() } && newValue.length <= 2) {
                        reloadAmount = newValue
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
            Button(modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally)
                .wrapContentHeight(Alignment.CenterVertically)
                .width(IntrinsicSize.Max),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.green_accent)
                ),
                enabled = reloadAmount.isNotEmpty(),
                onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        val amount = reloadAmount.toDouble() * 100.0
                        "https://stripe-api-eight.vercel.app/api/create-payment-intent".httpPost()
                            .header("Content-Type", "application/json")
                            .body("{ \"amount\": $amount }")
                            .responseJson { _, _, result ->
                                if (result is Result.Success) {
                                    val responseJson = result.get().obj()
                                    paymentIntentClientSecret =
                                        responseJson.getString("paymentIntentClientSecret")
                                    customerConfig = PaymentSheet.CustomerConfiguration(
                                        responseJson.getString("customerId"),
                                        responseJson.getString("ephemeralKeySecret")
                                    )
                                    val publishableKey = responseJson.getString("publishableKey")
                                    PaymentConfiguration.init(context, publishableKey)
                                    presentPaymentSheet(
                                        paymentSheet,
                                        customerConfig!!, paymentIntentClientSecret!!
                                    )
                                }
                            }
                    }
                }
            )
            {
                Text(
                    modifier = Modifier,
                    text = "Nadoplati",
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

        reloadFlow.value?.let{
            when(it)
            {
                is Resource.Failure -> Toast.makeText(context, "Na žalost nismo uspjeli izvršiti transakciju.", Toast.LENGTH_LONG).show()
                is Resource.Success -> {
                    navController.popBackStack()
                    reloadViewModel.reload(Reload(reloadAmount = reloadAmount.toDouble()))
                    navController.navigate(Routes.Home.route)
                }

                Resource.Loading -> null
            }
        }


        /*Row(
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
                    text = "Nadopuni novčanik",
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
                    text = "Broj kratice",
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
                    value = ccNum,
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
                        if (newValue.all { it.isDigit() } && newValue.length <= 16) {
                            ccNum = newValue
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
                    text = "Datum i godina isteka",
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextField(
                        modifier = Modifier.width(50.dp),
                        value = ccDate,
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
                            if (newValue.all { it.isDigit() } && newValue.length <= 2) {
                                ccDate = newValue
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
                    Spacer(modifier = Modifier.size(20.dp))
                    TextField(
                        modifier = Modifier.width(50.dp),
                        value = ccYear,
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
                            if (newValue.all { it.isDigit() } && newValue.length <= 2) {
                                ccYear = newValue
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
                }
                Text(
                    text = "CVV",
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
                    value = cvvNum,
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
                        if (newValue.all { it.isDigit() } && newValue.length <= 3) {
                            cvvNum = newValue
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
                    text = "Vrijednost nadoplate",
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
                    value = reloadAmount,
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
                        if (newValue.all { it.isDigit() } && newValue.length <= 2) {
                            reloadAmount = newValue
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
                reloadFlow.value?.let {
                    when (it) {
                        is Resource.Failure -> {
                            errorText = when(it.exception.message.toString())
                            {
                                "BAD_CVV" -> "Loše unesen CVV"
                                "CARD_EXPIRED" -> "Kartica je istekla"
                                "BAD_AMOUNT" -> "Unesite vrijednost nadoplate"
                                "BAD_CARD" -> "Unesite broj kartice"
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
                            navController.navigate(Routes.Home.route)
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
                        reloadViewModel.reloadBalance(Reload(try { reloadAmount.toDouble()} catch(e: Exception) { Double.NaN }), ccYear, ccDate, cvvNum, ccNum)
                    })
                {
                    Text(
                        modifier = Modifier,
                        text = "Nadoplati",
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
        }*/
    }
}

private fun presentPaymentSheet(
    paymentSheet: PaymentSheet,
    customerConfig: PaymentSheet.CustomerConfiguration,
    paymentIntentClientSecret: String
) {
    paymentSheet.presentWithPaymentIntent(
        paymentIntentClientSecret,
        PaymentSheet.Configuration(
            merchantDisplayName = "Wuff",
            customer = customerConfig,
            allowsDelayedPaymentMethods = false
        )
    )
}