package com.filiptoprek.wuff.presentation.home

import android.app.DatePickerDialog
import android.widget.CalendarView
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.filiptoprek.wuff.R
import com.filiptoprek.wuff.domain.model.auth.Resource
import com.filiptoprek.wuff.domain.model.profile.UserProfile
import com.filiptoprek.wuff.domain.model.profile.Walker
import com.filiptoprek.wuff.domain.model.reservation.WalkType
import com.filiptoprek.wuff.presentation.auth.AuthViewModel
import com.filiptoprek.wuff.ui.theme.Opensans
import com.filiptoprek.wuff.ui.theme.Pattaya
import java.util.Date

@Composable
fun HomeScreen(
    navController: NavHostController,
    homeViewModel: HomeViewModel?
){
    val homeFlow = homeViewModel?.homeFlow?.collectAsState()
    val walkerList = homeViewModel?.walkerList?.collectAsState()
    var isLoading by remember { mutableStateOf(false) }

    homeFlow?.value?.let {
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
                    walkerTab(walkerList?.value!!)
                }
            }

        }
    }

}

@Composable
fun HomePreview()
{
    HomeScreen(navController = rememberNavController(), homeViewModel = null)
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
fun walkerTab(walkerList: List<UserProfile?>)
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
                color = Color.Black,
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
@Preview
@Composable
fun reserveWalk()
{
        var walkType by remember { mutableStateOf("") }
        var price by remember { mutableStateOf("") }
        var walkDate = rememberDatePickerState()
        var walkTime by remember { mutableStateOf(Date()) }
        var showDialog by remember { mutableStateOf(false) }

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
                    model = "",
                    placeholder = painterResource(id = R.drawable.user_placeholder),
                    error = painterResource(id = R.drawable.user_placeholder),
                    contentDescription = "User image",
                )
                Spacer(modifier = Modifier.size(20.dp))
                var expanded by remember { mutableStateOf(false) }
                var selectedText by remember { mutableStateOf("test") }
                var walkTypes by remember {
                    mutableStateOf(
                        listOf(
                            WalkType("Brza šetnja"),
                            WalkType("Spora štenja"),
                            WalkType("Duga štenja")
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp)
                ) {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = {
                            expanded = !expanded
                        }
                    ) {
                        TextField(
                            value = selectedText,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                        ) {
                            walkTypes.forEach { item ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = item.walkName.toString(),
                                            style = TextStyle(
                                                fontFamily = Opensans,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Normal,
                                                textAlign = TextAlign.Start,
                                                color = colorResource(R.color.gray),
                                            ),
                                            color = colorResource(R.color.gray)
                                        )
                                    },
                                    onClick = {
                                        selectedText = item.walkName.toString()
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // TODO: Replace with material library
                AndroidView(
                    { CalendarView(it) },
                    modifier = Modifier.wrapContentWidth(),
                    update = { views ->
                        views.setOnDateChangeListener { calendarView, year, month, dayOfMonth ->
                        }
                    }
                )
                /*// TimePicker
                TimePicker(
                    time = walkTime,
                    onTimeChange = { newTime -> walkTime = newTime },
                    label = { Text("Odaberite vrijeme šetnje") }
                )*/

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