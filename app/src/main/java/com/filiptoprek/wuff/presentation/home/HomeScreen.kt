package com.filiptoprek.wuff.presentation.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.filiptoprek.wuff.R
import com.filiptoprek.wuff.domain.model.Reload
import com.filiptoprek.wuff.domain.model.auth.Resource
import com.filiptoprek.wuff.domain.model.profile.UserProfile
import com.filiptoprek.wuff.presentation.profile.ProfileViewModel
import com.filiptoprek.wuff.presentation.reload.ReloadViewModel
import com.filiptoprek.wuff.presentation.reservation.ReservationViewModel
import com.filiptoprek.wuff.ui.theme.Opensans
import com.filiptoprek.wuff.ui.theme.Pattaya
import com.google.firebase.Timestamp

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    navController: NavHostController,
    homeViewModel: HomeViewModel,
    reservationViewModel: ReservationViewModel,
    profileViewModel: ProfileViewModel,
    reloadViewModel: ReloadViewModel
    ){
    val homeFlow = homeViewModel.homeFlow.collectAsState()
    val reservationCreateFlow = reservationViewModel.reservationCreateFlow.collectAsState()
    val walkerList = homeViewModel.walkerList.collectAsState()
    val walkTypeList = reservationViewModel.walkTypeList.collectAsState()

    var isReloading by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var reserved by remember { mutableStateOf(false) }
    val selectedWalker = remember { mutableStateOf(UserProfile()) }

    var selectedText = remember { mutableStateOf("Odaberite vrstu šetnje") }

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
                        val onReserve: (Boolean) -> Unit = { newValue ->
                            reserved = newValue
                        }
                        reserveWalk(selectedWalker.value, selectedText, walkTypeList.value, reservationViewModel, reservationCreateFlow, onReserve)
                    }
                }
            }
        }else if(isReloading){
            val onReload: (Boolean) -> Unit = { newValue ->
                isReloading = newValue
            }
            reloadWallet(reloadViewModel, onReload)
        }
        else{
            Column(
                modifier = Modifier.padding(horizontal = 40.dp)
            ) {
                Spacer(modifier = Modifier.size(20.dp))

                if(profileViewModel.userProfile?.walker != null)
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
                    val onReload: (Boolean) -> Unit = { newValue ->
                        isReloading = newValue
                    }
                    infoCard("Jednostavno i brzno nadopuni svoj novčanik", "Nadopuni", onReload)

                    Column(
                        modifier = Modifier.padding(horizontal = 40.dp)
                    ) {
                        Row (
                            modifier = Modifier
                                .fillMaxWidth()

                        ){
                            Text(text = "Šetači", color = Color.Black)
                            Spacer(Modifier.weight(1f))
                            Text(text = "Ostali>", color = Color.Black)
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
                            val onReserve: (Boolean) -> Unit = { newValue ->
                                reserved = newValue
                            }
                            walkerTab(walkerList.value, onReserve, selectedWalker)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun infoCard(text: String, buttonText: String, onReload: (Boolean) -> Unit)
{
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth()
            .padding(15.dp)
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
                    onReload(true)
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
fun walkerTab(walkerList: List<UserProfile?>, onReserve: (Boolean) -> Unit, selectedWalker : MutableState<UserProfile>)
{
    walkerList.forEach{walker ->
        Row (
            modifier = Modifier
                .padding(top = 15.dp)
                .wrapContentWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,

            ){
            AsyncImage(
                modifier = Modifier
                    .clip(RoundedCornerShape(90.dp))
                    .height(40.dp),
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
                ))
            Spacer(modifier = Modifier.weight(1f))
            Button(modifier = Modifier.size(width = 115.dp, height = 40.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.green_accent)
                ),
                contentPadding = PaddingValues(0.dp),
                onClick = {
                    onReserve(true)
                    selectedWalker.value = walker!!
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun reloadWallet(reloadViewModel: ReloadViewModel, onReload: (Boolean) -> Unit)
{
    var reloadFlow = reloadViewModel.reloadFlow.collectAsState()
    var ccNum by remember { mutableStateOf("") }
    var reloadAmount by remember { mutableStateOf("") }
    var cvvNum by remember { mutableStateOf("") }
    var ccDate by remember { mutableStateOf("") }
    var ccYear by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf("") }

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
                onValueChange = {newValue ->
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
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ){
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
                        onValueChange = {newValue ->
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
                        onValueChange = {newValue ->
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
                modifier = Modifier.width(50.dp),
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
                onValueChange = {newValue ->
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
                onValueChange = {newValue ->
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
                when(it){
                    is Resource.Failure -> {
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
                    reloadViewModel.reloadBalance(Reload(reloadAmount.toDouble()))
                    onReload(false)
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
                text = "${profileViewModel.userProfile?.walker?.averageRating}/5",
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