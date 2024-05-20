package com.filiptoprek.wuff.presentation.home

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerColors
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.filiptoprek.wuff.R
import com.filiptoprek.wuff.domain.model.auth.Resource
import com.filiptoprek.wuff.domain.model.profile.UserProfile
import com.filiptoprek.wuff.domain.model.reservation.Reservation
import com.filiptoprek.wuff.domain.model.reservation.WalkType
import com.filiptoprek.wuff.presentation.reservation.ReservationViewModel
import com.filiptoprek.wuff.ui.theme.Opensans
import com.filiptoprek.wuff.ui.theme.Pattaya
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    navController: NavHostController,
    homeViewModel: HomeViewModel,
    reservationViewModel: ReservationViewModel,
    ){
    val homeFlow = homeViewModel.homeFlow.collectAsState()
    val reservationFlow = reservationViewModel.reservationFlow.collectAsState()
    val walkerList = homeViewModel.walkerList.collectAsState()
    val walkTypeList = reservationViewModel.walkTypeList.collectAsState()

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
                        reserveWalk(selectedWalker.value, selectedText, walkTypeList.value, reservationViewModel, reservationFlow, onReserve)
                    }
                }
            }
        }else
        {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.size(20.dp))
                infoCard()
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

@Composable
fun infoCard()
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
                text = "Dostupne šetnje u vašem susjedstvu",
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
                })
            {
                Text(
                    modifier = Modifier,
                    text = "Provjeri",
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


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun reserveWalk(selectedWalker: UserProfile?, selectedText: MutableState<String>, walkTypes: List<WalkType?>, reservationViewModel: ReservationViewModel, reservationFlow: State<Resource<Any>?>, onReserve: (Boolean) -> Unit)
{
    var dateString = remember {
        mutableStateOf("")
    }

    var timeString = remember {
        mutableStateOf("")
    }

    var isError by remember { mutableStateOf(false) }
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
                    text = "Rezerviraj šetnju",
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
                    model = selectedWalker?.user?.profilePhotoUrl,
                    placeholder = painterResource(id = R.drawable.user_placeholder),
                    error = painterResource(id = R.drawable.user_placeholder),
                    contentDescription = "User image",
                )
                Spacer(modifier = Modifier.size(15.dp))
                Text(
                    text = selectedWalker?.user?.name.toString(),
                    style = TextStyle(
                        fontFamily = Opensans,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = colorResource(R.color.gray)
                    )
                )
                Spacer(modifier = Modifier.size(10.dp))
                if (isError){
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
                }
                dropDownMenu(selectedText, walkTypes)

                dateTimePickers(dateString, timeString)

                Spacer(modifier = Modifier.size(10.dp))
                Text(
                    text = if (selectedText.value != "Odaberite vrstu šetnje") {
                        "${walkTypes.find { it?.walkName == selectedText.value }?.walkPrice}€"
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

                        if(dateString.value.isEmpty())
                        {
                            isError = true
                            errorText = "Molimo unesite datum šetnje"
                            return@Button
                        }

                        if(timeString.value.isEmpty())
                        {
                            isError = true
                            errorText = "Molimo unesite vrijeme šetnje"
                            return@Button
                        }

                        reservationViewModel.createReservation(Reservation("", selectedWalker?.user?.uid.toString(),"", dateString.value
                                , timeString.value,false,false, walkTypes.find { it?.walkName == selectedText.value }?.walkPrice!!,
                            walkType = walkTypes.find { type -> type?.walkName == selectedText.value }!!
                        ))
                        onReserve(false)
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

@Composable
fun TimePickerDialog(
    title: String = "Odaberite vrijeme",
    onDismissRequest: () -> Unit,
    confirmButton: @Composable (() -> Unit),
    dismissButton: @Composable (() -> Unit)? = null,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = containerColor
                ),
            color = containerColor
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = title,
                    style = TextStyle(
                        fontFamily = Opensans,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    ),
                    color = colorResource(R.color.gray)
                )
                content()
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    dismissButton?.invoke()
                    confirmButton()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun dropDownMenu(selectedText: MutableState<String>, walkTypes: List<WalkType?>)
{
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
    ) {
        MaterialTheme(
            colorScheme = MaterialTheme.colorScheme.copy(surface = colorResource(R.color.background_white), surfaceTint = Color.Transparent),
            shapes = MaterialTheme.shapes.copy(medium = RoundedCornerShape(20))
        ) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                }
            ) {
                TextField(
                    colors = TextFieldDefaults.textFieldColors(
                        unfocusedTextColor = colorResource(R.color.gray),
                        containerColor = Color.White,
                        cursorColor = colorResource(R.color.green_accent),
                        disabledLabelColor = Color.Transparent,
                        focusedIndicatorColor = colorResource(R.color.green_accent),
                        unfocusedIndicatorColor = colorResource(R.color.green_accent),
                        unfocusedLabelColor = colorResource(R.color.gray),
                        focusedTextColor = colorResource(R.color.gray),
                        focusedLabelColor = colorResource(R.color.gray),
                        selectionColors = TextSelectionColors(
                            backgroundColor = colorResource(R.color.background_white),
                            handleColor = colorResource(R.color.green_accent),
                        ),
                    ),
                    label = {
                        Text(
                            text = "Vrsta šetnje",
                            style = TextStyle(
                                fontFamily = Opensans,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Thin,
                                textAlign = TextAlign.Start,
                                color = colorResource(R.color.gray),
                            ),
                            color = colorResource(R.color.gray),
                        )
                    },
                    textStyle = TextStyle(
                        fontFamily = Opensans,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Thin,
                        textAlign = TextAlign.Start,
                        color = colorResource(R.color.gray),
                    ),
                    value = selectedText.value,
                    onValueChange = {},
                    readOnly = true,

                    modifier = Modifier.menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    walkTypes.forEach { item ->
                        DropdownMenuItem(
                            colors = MenuItemColors(
                                textColor = colorResource(R.color.gray),
                                disabledTextColor = colorResource(R.color.box_bkg_white),
                                leadingIconColor = Color.Transparent,
                                trailingIconColor = Color.Transparent,
                                disabledLeadingIconColor = Color.Transparent,
                                disabledTrailingIconColor = Color.Transparent
                            ),
                            text = {
                                Text(
                                    text = item?.walkName.toString(),
                                    style = TextStyle(
                                        fontFamily = Opensans,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Normal,
                                        textAlign = TextAlign.Start,
                                        color = colorResource(R.color.gray),
                                    ),
                                    color = colorResource(R.color.gray),
                                )
                            },
                            onClick = {
                                selectedText.value = item?.walkName.toString()
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun dateTimePickers(dateString: MutableState<String>, timeString: MutableState<String>)
{
    val context = LocalContext.current

    val date = remember {
        Calendar.getInstance().apply {
            set(Calendar.YEAR, 2024)
            set(Calendar.MONTH, 5)
            set(Calendar.DAY_OF_MONTH, 17)
        }.timeInMillis
    }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = date,
        yearRange = 2024..2024
    )
    var showDatePicker by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState(
        initialHour = 14,
        initialMinute = 30,
        is24Hour = true,
    )
    var showTimePicker by remember { mutableStateOf(false) }


    //date picker
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .wrapContentHeight(Alignment.CenterVertically)
            .width(IntrinsicSize.Max),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(R.color.green_accent)
        ),
        onClick = {
            showDatePicker = true //changing the visibility state
        },
    ) {
        Text(
            text =
            if (dateString.value.isEmpty()) {
                "Odaberite datum"
            } else {
                dateString.value
            },
            style = TextStyle(
                fontFamily = Opensans,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color.White
            ))
    }

    //time picker
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .wrapContentHeight(Alignment.CenterVertically)
            .width(IntrinsicSize.Max),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(R.color.green_accent)
        ),
        onClick = {
            showTimePicker = true //changing the visibility state
        },
    ) {
        Text(
            text = if(timeString.value.isEmpty())
            {
                "Odaberite vrijeme"
            }else
            {
                timeString.value
            },
            style = TextStyle(
                fontFamily = Opensans,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color.White
            ))
    }

    if (showDatePicker) {
        DatePickerDialog(
            colors = DatePickerDefaults.colors(
                containerColor = colorResource(R.color.white),
            ),
            onDismissRequest = { /*TODO*/ },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedDate = Calendar.getInstance().apply {
                            timeInMillis = datePickerState.selectedDateMillis!!
                            set(Calendar.HOUR_OF_DAY, 0)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }

                        val year = selectedDate.get(Calendar.YEAR)
                        val month = selectedDate.get(Calendar.MONTH) + 1 // Months are zero-based!!!!!!
                        val dayOfMonth = selectedDate.get(Calendar.DAY_OF_MONTH)

                        val formattedMonth = String.format("%02d", month)
                        val formattedDay = String.format("%02d", dayOfMonth)

                        dateString.value = "$formattedDay/$formattedMonth/$year"

                        if (selectedDate.timeInMillis >= Calendar.getInstance().apply {
                                set(Calendar.HOUR_OF_DAY, 0)
                                set(Calendar.MINUTE, 0)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0) }.timeInMillis) {
                            Toast.makeText(
                                context,
                                "Odabrani datum ${dateString.value}",
                                Toast.LENGTH_SHORT
                            ).show()
                            showDatePicker = false
                        } else {
                            dateString.value = ""
                            Toast.makeText(
                                context,
                                "Odabrani datum mora biti danas ili u budućnosti",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                ) { Text(
                    text = "Odaberi",
                    style = TextStyle(
                        fontFamily = Opensans,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )) }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                    }
                ) { Text(
                    text = "Odustani",
                    style = TextStyle(
                        fontFamily = Opensans,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )) }
            }
        )
        {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    todayContentColor = colorResource(R.color.gray),
                    todayDateBorderColor = colorResource(R.color.gray),
                    selectedDayContentColor = colorResource(R.color.green_accent),
                    dayContentColor = colorResource(R.color.gray),
                    selectedDayContainerColor = colorResource(R.color.box_bkg_white),
                    dividerColor = colorResource(R.color.gray),
                    containerColor = colorResource(R.color.box_bkg_white),
                    currentYearContentColor = colorResource(R.color.gray),
                    weekdayContentColor = colorResource(R.color.gray),
                    titleContentColor = colorResource(R.color.gray),
                    headlineContentColor = colorResource(R.color.gray),
                    yearContentColor = colorResource(R.color.gray),
                    subheadContentColor = colorResource(R.color.box_bkg_white),
                    selectedYearContainerColor = colorResource(R.color.gray),
                    navigationContentColor = colorResource(R.color.gray),
                    selectedYearContentColor = colorResource(R.color.gray),
                    dateTextFieldColors = TextFieldDefaults.textFieldColors(
                        unfocusedTextColor = colorResource(R.color.gray),
                        focusedTextColor = colorResource(R.color.gray),
                        containerColor  = colorResource(R.color.box_bkg_white),
                        cursorColor = colorResource(R.color.green_accent),
                        unfocusedLabelColor = colorResource(R.color.gray),
                        focusedLabelColor = colorResource(R.color.gray),
                        disabledLabelColor = Color.Transparent,
                        focusedIndicatorColor =  Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent)
                )
            )
        }
    }

    if (showTimePicker) {
        TimePickerDialog(
            containerColor = colorResource(R.color.white),
            onDismissRequest = { /*TODO*/ },
            confirmButton = {
                TextButton(
                    onClick = {
                        val hours = timePickerState.hour
                        val minutes = timePickerState.minute

                        val currentTime = Calendar.getInstance()

                        timeString.value = "${hours}:${minutes}"

                        val year = currentTime.get(Calendar.YEAR)
                        val month = currentTime.get(Calendar.MONTH) + 1 // Months are zero-based!!!!!!
                        val dayOfMonth = currentTime.get(Calendar.DAY_OF_MONTH)
                        val currentDate = "$dayOfMonth/$month/$year"

                        if (((hours <= currentTime.time.hours) && dateString.value == currentDate)) {
                            timeString.value = ""
                            Toast.makeText(
                                context,
                                "Odabrano vijreme mora biti u budućnosti",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                context,
                                "Odabrano vrijeme je ${hours}:${minutes}",
                                Toast.LENGTH_SHORT
                            ).show()
                            showTimePicker = false
                        }
                    }
                ) { Text(
                    text = "Odaberi",
                    style = TextStyle(
                        fontFamily = Opensans,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )) }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showTimePicker = false
                    }
                ) { Text(
                    text = "Odustani",
                    style = TextStyle(
                        fontFamily = Opensans,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )) }
            }
        )
        {
            TimePicker(
                state = timePickerState,
                colors = TimePickerColors(
                    containerColor = colorResource(R.color.background_white),
                    selectorColor = colorResource(R.color.box_bkg_white),
                    clockDialColor = colorResource(R.color.background_white),
                    clockDialUnselectedContentColor = colorResource(R.color.gray),
                    clockDialSelectedContentColor = colorResource(R.color.green_accent),
                    periodSelectorSelectedContentColor = colorResource(R.color.green_accent),
                    periodSelectorBorderColor = colorResource(R.color.box_bkg_white),
                    periodSelectorUnselectedContainerColor = colorResource(R.color.background_white),
                    periodSelectorSelectedContainerColor = colorResource(R.color.background_white),
                    periodSelectorUnselectedContentColor = colorResource(R.color.box_bkg_white),
                    timeSelectorSelectedContainerColor = colorResource(R.color.background_white),
                    timeSelectorUnselectedContentColor = colorResource(R.color.gray),
                    timeSelectorSelectedContentColor = colorResource(R.color.green_accent),
                    timeSelectorUnselectedContainerColor = colorResource(R.color.background_white),
                )
            )
        }
    }
}