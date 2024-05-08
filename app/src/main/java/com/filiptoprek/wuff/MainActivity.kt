package com.filiptoprek.wuff

import android.annotation.SuppressLint
import android.graphics.Color.parseColor
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFrom
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialogDefaults.containerColor
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.filiptoprek.wuff.ui.theme.Opensans
import com.filiptoprek.wuff.ui.theme.Pattaya
import com.filiptoprek.wuff.ui.theme.WuffTheme

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WuffTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                ) {
                    LandingView()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LandingView(){
    Column(
        modifier = Modifier
            .background(Color(parseColor("#081C15")))
            .fillMaxSize()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .wrapContentHeight(Alignment.Top)
            .padding(20.dp)
        ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally),
            text = "Wuff!",
            style = TextStyle(
                fontFamily = Pattaya,
                fontSize =  135.sp,
                lineHeight = 27.sp,
                color = Color.White
            )
        )
        Image(painterResource(R.drawable.dog),"dog image",
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally)
                .offset(0.dp, (-80).dp)
        )

        Column(modifier = Modifier
            .background(Color(parseColor("#081C15")))
            .fillMaxSize()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .wrapContentHeight(Alignment.Top)
        ){
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally),
                text = "Savršeno usklađena šetnja za tvojeg ljubimca.",
                style = TextStyle(
                    fontFamily = Opensans,
                    fontSize =  24.sp,
                    lineHeight = 27.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
            )
            Button(modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally)
                .padding(top = 60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(parseColor("#52B788"))),
                onClick = { /*TODO*/ })
            {
                Text(
                    modifier = Modifier.padding(5.dp),
                    text = "Započni",
                    style = TextStyle(
                        fontFamily = Opensans,
                        fontSize =  24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                )
            }
            Button(modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally)
                .padding(top = 20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                onClick = { /*TODO*/ })
            {
                Text(
                    modifier = Modifier.padding(5.dp),
                    text = "Prijavi se",
                    style = TextStyle(
                        fontFamily = Opensans,
                        fontSize =  24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                )
            }
        }
    }

}