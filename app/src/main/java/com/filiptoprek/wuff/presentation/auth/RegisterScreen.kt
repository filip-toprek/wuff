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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.filiptoprek.wuff.R
import com.filiptoprek.wuff.navigation.Routes
import com.filiptoprek.wuff.domain.model.auth.Resource
import com.filiptoprek.wuff.ui.theme.AppTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun RegisterScreen(
    navController: NavHostController,
    viewModel: AuthViewModel?,
    googleSignInClient: GoogleSignInClient?
) {
    var nameText by remember { mutableStateOf("") }
    var emailText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }
    var verifyPasswordText by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf("") }

    val registerFlow = viewModel?.registerFlow?.collectAsState()
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
            .background(colorResource(R.color.background_dark))
            .fillMaxSize()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .wrapContentHeight(Alignment.Top)
    ) {
        AuthTitle()
        Spacer(modifier = Modifier.size(AppTheme.dimens.customSpacing))
        RegisterForm(
            errorText,
            {errorText = it},
            nameText, {nameText = it},
            emailText, {emailText = it},
            passwordText, {passwordText = it},
            verifyPasswordText, {verifyPasswordText = it},
            viewModel)

        registerFlow?.value?.let {
            when (it) {
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
                    LaunchedEffect(Unit) {
                        navController.navigate(Routes.Home.route) {
                            popUpTo(Routes.LandingScreen.route) { inclusive = true }
                        }
                    }
                }
            }
        }


        GoogleSignInButton(text = "Registriraj") {
            launcher.launch(googleSignInClient?.signInIntent!!)
        }
        RegisterFooter(navController)
    }
}

@Composable
fun RegisterForm(
    errorText: String,
    onErrorChange: (String)->Unit,
    nameText: String,
    onNameChange: (String)->Unit,
    emailText: String,
    onEmailChange: (String)->Unit,
    passwordText: String,
    onPasswordChange: (String)->Unit,
    verifyPasswordText: String,
    onVerifyPasswordChange: (String)->Unit,
    authViewModel: AuthViewModel?
    ) {

    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally),
        value = nameText,
        onValueChange = { onNameChange(it) },
        textStyle = MaterialTheme.typography.labelSmall,
        label = {
            Text("Ime i prezime",
                color = colorResource(R.color.gray),
                style = MaterialTheme.typography.bodySmall
            )
        },
        shape = RoundedCornerShape(8.dp),
        colors = TextFieldDefaults.textFieldColors(
            unfocusedTextColor = colorResource(R.color.gray),
            containerColor = Color.White,
            cursorColor = colorResource(R.color.background_dark),
            disabledLabelColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = colorResource(R.color.gray),
        ),
        supportingText = {
            if (errorText == "BAD_NAME") {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Molimo unesite vaše ime i prezime",
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
        value = emailText,
        onValueChange = { onEmailChange(it) },
        textStyle = MaterialTheme.typography.labelSmall,
        label = {
            Text("Email",
                color = colorResource(R.color.gray),
                style = MaterialTheme.typography.bodySmall
            )
        },
        shape = RoundedCornerShape(8.dp),
        colors = TextFieldDefaults.textFieldColors(
            unfocusedTextColor = colorResource(R.color.gray),
            containerColor = Color.White,
            cursorColor = colorResource(R.color.background_dark),
            disabledLabelColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = colorResource(R.color.gray)
        ),
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
            .wrapContentWidth(Alignment.CenterHorizontally) ,
        value = passwordText,
        onValueChange = { onPasswordChange(it) },
        textStyle = MaterialTheme.typography.labelSmall,
        visualTransformation = PasswordVisualTransformation(),
        label = {
            Text("Lozinka",
                color = colorResource(R.color.gray),
                style = MaterialTheme.typography.bodySmall
            )
        },
        shape = RoundedCornerShape(8.dp),
        colors = TextFieldDefaults.textFieldColors(
            unfocusedTextColor = colorResource(R.color.gray),
            containerColor = Color.White,
            cursorColor = colorResource(R.color.background_dark),
            disabledLabelColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = colorResource(R.color.gray)
        ),
        supportingText = {
            if (errorText == "BAD_PASSWORD") {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Lozinka mora sadržavati 8 znakova i jedan broj",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Red
                )
            }
        }
    )

    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally) ,
        value = verifyPasswordText,
        onValueChange = { onVerifyPasswordChange(it) },
        visualTransformation = PasswordVisualTransformation(),
        textStyle = MaterialTheme.typography.labelSmall,
        label = {
            Text("Potvrdi lozinku",
                color = colorResource(R.color.gray),
                style = MaterialTheme.typography.bodySmall
            )
        },
        shape = RoundedCornerShape(8.dp),
        colors = TextFieldDefaults.textFieldColors(
            unfocusedTextColor = colorResource(R.color.gray),
            containerColor = Color.White,
            cursorColor = colorResource(R.color.background_dark),
            disabledLabelColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = colorResource(R.color.gray)
        ),
        supportingText = {
            if (errorText == "NO_MATCH") {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Lozinke se moraju podudarati",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Red
                )
            }
        }
    )

    Button(modifier = Modifier
        .fillMaxWidth()
        .wrapContentWidth(Alignment.CenterHorizontally)
        .padding(AppTheme.dimens.medium),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(parseColor("#52B788"))),
        onClick = {
            onErrorChange("")
            authViewModel?.register(nameText, emailText, passwordText, verifyPasswordText)
        })
    {
        Text(
            modifier = Modifier.padding(5.dp),
            text = "Registriraj se",
            style = MaterialTheme.typography.displayMedium.copy(color = Color.White),
        )
    }
}

@Composable
fun RegisterFooter(navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .padding(AppTheme.dimens.mediumLarge),
    ) {
        Text(
            modifier = Modifier.padding(5.dp),
            text = "Već imaš Wuff! račun?",
            style = MaterialTheme.typography.bodySmall.copy(color = Color.White),
        )
        ClickableText(
            modifier = Modifier.padding(5.dp),
            text = AnnotatedString("Prijavi se"),
            style = MaterialTheme.typography.bodySmall.copy(color = colorResource(R.color.green_accent)),
            onClick = {
                navController.navigate(Routes.Login.route)
            }
        )
    }
}