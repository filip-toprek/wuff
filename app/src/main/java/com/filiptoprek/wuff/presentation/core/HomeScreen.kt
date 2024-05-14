package com.filiptoprek.wuff.presentation.core

import android.widget.Space
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.filiptoprek.wuff.R
import com.filiptoprek.wuff.navigation.Routes
import com.filiptoprek.wuff.presentation.auth.AuthViewModel
import com.filiptoprek.wuff.ui.theme.Opensans
import com.filiptoprek.wuff.ui.theme.Pattaya

@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: AuthViewModel?
){
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
                walkerTab()
            }

        }
    }

}

@Composable
fun HomePreview()
{
    HomeScreen(navController = rememberNavController(), viewModel = null)
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

@Preview
@Composable
fun walkerTab()
{
    Row (
        modifier = Modifier.padding(top = 15.dp)
            .wrapContentWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,

    ){
        AsyncImage(
            modifier = Modifier.clip(RoundedCornerShape(30.dp)).height(40.dp),
            model = "viewModel?.currentUser?.photoUrl",
            placeholder = painterResource(id = R.drawable.user_placeholder),
            error = painterResource(id = R.drawable.user_placeholder),
            contentDescription = "User image",
        )
        Spacer(modifier = Modifier.size(35.dp))
        Text(text = "Johane Doe", color = Color.Black)
        Spacer(modifier = Modifier.size(35.dp))
        Button(modifier = Modifier.size(width = 115.dp, height = 40.dp),
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
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
            )
        }
    }
}