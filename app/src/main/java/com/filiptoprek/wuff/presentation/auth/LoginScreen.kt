@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.filiptoprek.wuff.presentation.auth

import android.content.Intent
import android.graphics.Color.parseColor
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.filiptoprek.wuff.R
import com.filiptoprek.wuff.navigation.Routes
import com.filiptoprek.wuff.domain.model.auth.Resource
import com.filiptoprek.wuff.ui.theme.AppTheme
import com.filiptoprek.wuff.ui.theme.Opensans
import com.filiptoprek.wuff.ui.theme.Pattaya
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Preview
@Composable
fun PreviewLogin(){
    LoginScreen(navController = rememberNavController(), viewModel = null, googleSignInClient = null)
}
@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: AuthViewModel?,
    googleSignInClient: GoogleSignInClient?
) {
    var emailText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf("") }

    val loginFlow = viewModel?.loginFlow?.collectAsState()
    var user by remember { mutableStateOf(Firebase.auth.currentUser) }

    @Composable
    fun rememberFirebaseAuthLauncher(
        onAuthComplete: (AuthResult) -> Unit,
        onAuthError: (ApiException) -> Unit
    ): ManagedActivityResultLauncher<Intent, ActivityResult> {
        val scope = rememberCoroutineScope()
        return rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            viewModel?.signInWithGoogle(task, scope)
        }
    }

    val launcher = rememberFirebaseAuthLauncher(
        onAuthComplete = { result ->
            user = result.user
        },
        onAuthError = {
            user = null
        }
    )


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
                fontFamily = MaterialTheme.typography.titleLarge.fontFamily,
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                fontWeight = MaterialTheme.typography.titleLarge.fontWeight,
                textAlign = MaterialTheme.typography.titleLarge.textAlign,
                color = Color.White
            ),
        )
        Spacer(modifier = Modifier.size(AppTheme.dimens.customSpacing))
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally) ,
            text = if(errorText.contains("|")) errorText.split("|")[1] else "",
            style = TextStyle(
                fontFamily = Opensans,
                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                lineHeight = 5.sp,
                color = Color.Red
            )
        )
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally)
                .padding(top = 80.dp),
            value = emailText,
            onValueChange = { emailText = it },
            textStyle = MaterialTheme.typography.labelSmall,
            label = {
                Text("Email",
                    color = Color(parseColor("#333333")),
                    style = MaterialTheme.typography.bodySmall
                ) },
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.textFieldColors(
                unfocusedTextColor = Color(parseColor("#333333")),
                containerColor  = Color.White,
                cursorColor = Color(parseColor("#52B788")),
                disabledLabelColor = Color.Transparent,
                focusedIndicatorColor =  Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = colorResource(R.color.gray)),
            supportingText = {
                if (errorText == "BAD_EMAIL") {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Molimo unesite vašu email adresu",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Red
                    )
                }
            }
        )

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally),
            value = passwordText,
            onValueChange = { passwordText = it },
            textStyle = MaterialTheme.typography.labelSmall,
            visualTransformation = PasswordVisualTransformation(),
            label = {
                Text("Lozinka",
                    color = Color(parseColor("#333333")),
                    style = MaterialTheme.typography.bodySmall
                ) },
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.textFieldColors(
                unfocusedTextColor = Color(parseColor("#333333")),
                containerColor  = Color.White,
                cursorColor = Color(parseColor("#52B788")),
                disabledLabelColor = Color.Transparent,
                focusedIndicatorColor =  Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = colorResource(R.color.gray)),
            supportingText = {
                if (errorText == "BAD_PASSWORD") {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Molimo unesite lozinku",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Red
                    )
                }
            }
        )

        loginFlow?.value?.let {
            when(it){
                is Resource.Failure -> {
                    isLoading = false
                    errorText = it.exception.message.toString()
                }
                Resource.Loading -> {
                    isLoading = true
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally)
                            .wrapContentHeight(Alignment.CenterVertically),
                        color = colorResource(R.color.green_accent)
                    )
                }
                is Resource.Success -> {
                    isLoading = false
                    LaunchedEffect(Unit) {
                        navController.navigate(Routes.Home.route){
                            popUpTo(Routes.LandingScreen.route) { inclusive = true }
                        }
                    }
                }
            }
        }

        Button(modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .padding(AppTheme.dimens.largeText),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(parseColor("#52B788"))),
            onClick = {
                errorText = ""
                viewModel?.login(emailText, passwordText)
            },
            enabled = !isLoading)
        {
            Text(
                modifier = Modifier.padding(5.dp),
                text = "Prijavi se",
                style = TextStyle(
                    fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
                    fontSize = MaterialTheme.typography.displayMedium.fontSize,
                    fontWeight = MaterialTheme.typography.displayMedium.fontWeight,
                    textAlign = MaterialTheme.typography.displayMedium.textAlign,
                    color = Color.White
                ),
            )
        }


        GoogleSignInButton(text = "Prijavi") {
            launcher.launch(googleSignInClient?.signInIntent!!)
        }


        Row(modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .padding(AppTheme.dimens.mediumLarge)){
            Text(
                modifier = Modifier.padding(5.dp),
                text = "Još nemaš Wuff! račun?",
                style = TextStyle(
                    fontFamily = MaterialTheme.typography.bodySmall.fontFamily,
                    fontSize = MaterialTheme.typography.bodySmall.fontSize,
                    fontWeight = MaterialTheme.typography.bodySmall.fontWeight,
                    textAlign = MaterialTheme.typography.bodySmall.textAlign,
                    color = Color.White
                ),
            )
            ClickableText(
                modifier = Modifier.padding(5.dp),
                text = AnnotatedString("Registriraj se"),
                style = TextStyle(
                    fontFamily = MaterialTheme.typography.bodySmall.fontFamily,
                    fontSize = MaterialTheme.typography.bodySmall.fontSize,
                    fontWeight = MaterialTheme.typography.bodySmall.fontWeight,
                    textAlign = MaterialTheme.typography.bodySmall.textAlign,
                    color = Color(parseColor("#52B788"))
                ),
                onClick = {
                    navController.navigate(Routes.Register.route)
                }
            )
        }
    }
}

@Composable
fun GoogleSignInButton(text: String = "Prijavi", onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .padding(10.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(parseColor("#52B788")))
    ) {
        Icon(
            ImageVector.vectorResource(id = R.drawable.ic_google_logo),
            contentDescription = "$text se sa Google računom",
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("$text se sa Google računom", color = Color.White)
    }
}