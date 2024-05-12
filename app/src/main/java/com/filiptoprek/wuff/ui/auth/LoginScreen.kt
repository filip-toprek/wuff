@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.filiptoprek.wuff.ui.auth

import android.graphics.Color.parseColor
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.filiptoprek.wuff.Routes
import com.filiptoprek.wuff.auth.domain.model.Resource
import com.filiptoprek.wuff.ui.theme.Opensans
import com.filiptoprek.wuff.ui.theme.Pattaya
import kotlin.math.log

@Preview
@Composable
fun PreviewLogin(){
    LoginScreen(navController = rememberNavController(), viewModel = null)
}
@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: AuthViewModel?
) {
    var emailText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val loginFlow = viewModel?.loginFlow?.collectAsState()

    Column(
        modifier = Modifier
            .background(Color(android.graphics.Color.parseColor("#081C15")))
            .fillMaxSize()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .wrapContentHeight(Alignment.Top)
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally),
            text = "Wuff!",
            style = TextStyle(
                fontFamily = Pattaya,
                fontSize = 135.sp,
                lineHeight = 27.sp,
                color = Color.White
            )
        )
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally)
                .padding(top = 80.dp),
            value = emailText,
            onValueChange = { emailText = it },
            label = { Text("Email", color = Color(parseColor("#333333"))) },
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.textFieldColors(
                textColor = Color(parseColor("#333333")),
                containerColor  = Color.White,
                cursorColor = Color(parseColor("#52B788")),
                disabledLabelColor = Color.Transparent,
                focusedIndicatorColor =  Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent)
        )

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally)
                .padding(top = 10.dp),
            value = passwordText,
            onValueChange = { passwordText = it },
            visualTransformation = PasswordVisualTransformation(),
            label = { Text("Lozinka", color = Color(parseColor("#333333"))) },
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.textFieldColors(
                textColor = Color(parseColor("#333333")),
                containerColor  = Color.White,
                cursorColor = Color(parseColor("#52B788")),
                disabledLabelColor = Color.Transparent,
                focusedIndicatorColor =  Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent)
        )

        loginFlow?.value?.let {
            when(it){
                is Resource.Faliure -> {
                    isLoading = false
                    val context = LocalContext.current
                    Toast.makeText(context, it.exception.message, Toast.LENGTH_LONG).show()
                }
                Resource.Loading -> {
                    isLoading = true
                    CircularProgressIndicator(modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                        .wrapContentHeight(Alignment.CenterVertically)
                    )
                }
                is Resource.Success -> {
                    isLoading = false
                    LaunchedEffect(Unit) {
                        navController.navigate(Routes.Home.route){
                            popUpTo(Routes.LandingScreen.route) { inclusive = true}
                        }
                    }
                }
            }
        }


        Button(modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .padding(top = 35.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(parseColor("#52B788"))),
            onClick = {
                viewModel?.login(emailText, passwordText)
            },
            enabled = !isLoading)
        {
            Text(
                modifier = Modifier.padding(5.dp),
                text = "Prijavi se",
                style = TextStyle(
                    fontFamily = Opensans,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
            )
        }
        Row(modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .padding(top = 35.dp)){
            Text(
                modifier = Modifier.padding(5.dp),
                text = "Još nemaš Wuff! račun?",
                style = TextStyle(
                    fontFamily = Opensans,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
            )
            ClickableText(
                modifier = Modifier.padding(5.dp),
                text = AnnotatedString("Registriraj se"),
                style = TextStyle(
                    fontFamily = Opensans,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = Color(parseColor("#52B788"))
                ),
                onClick = {
                    navController.navigate(Routes.Register.route)
                }
            )
        }
    }
}