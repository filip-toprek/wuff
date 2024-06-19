package com.filiptoprek.wuff.presentation.auth

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filiptoprek.wuff.data.utils.await
import com.filiptoprek.wuff.domain.model.auth.Resource
import com.filiptoprek.wuff.domain.repository.auth.AuthRepository
import com.filiptoprek.wuff.domain.usecase.auth.FormValidatorUseCase
import com.filiptoprek.wuff.service.LocationService
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.internal.wait
import javax.inject.Inject


@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val formValidatorUseCase: FormValidatorUseCase,
    private val googleSignInClient: GoogleSignInClient
) : ViewModel() {
    private val _loginFlow = MutableStateFlow<Resource<FirebaseUser>?>(null)
    val loginFlow: StateFlow<Resource<FirebaseUser>?> = _loginFlow

    private val _registerFlow = MutableStateFlow<Resource<FirebaseUser>?>(null)
    val registerFlow: StateFlow<Resource<FirebaseUser>?> = _registerFlow

    val currentUserLiveData: LiveData<FirebaseUser?>
        get() = authRepository.currentUserLiveData

    val currentUser: FirebaseUser?
        get() = authRepository.currentUser

    init {
        if(authRepository.currentUser != null){
            _loginFlow.value = Resource.Success(authRepository.currentUser!!)
        }
    }

    // login with email and password
    fun login(email: String, password: String) = viewModelScope.launch {
        when(formValidatorUseCase.validateForm(email = email, password = password))
        {
            // handle failure
            -2 -> _loginFlow.value = Resource.Failure(Exception("BAD_EMAIL"))
            -3 -> _loginFlow.value = Resource.Failure(Exception("BAD_PASSWORD"))

            // handle success
            1 -> {
                _loginFlow.value = Resource.Loading
                val result = authRepository.login(email, password)
                _loginFlow.value = result
            }
        }
    }

    // register with email and password
    fun register(name: String, email: String, password: String, verifyPassword: String) = viewModelScope.launch {
        when(formValidatorUseCase.validateForm(name, email, password, verifyPassword))
        {
            // handle failure
            -1 -> _registerFlow.value = Resource.Failure(Exception("BAD_NAME"))
            -2 -> _registerFlow.value = Resource.Failure(Exception("BAD_EMAIL"))
            -3 -> _registerFlow.value = Resource.Failure(Exception("BAD_PASSWORD"))
            -4 -> _registerFlow.value = Resource.Failure(Exception("NO_MATCH"))

            // handle success
            1 -> {
                _registerFlow.value = Resource.Loading
                val result = authRepository.register(name, email, password)
                _registerFlow.value = result
            }
        }
    }

    // logout
    fun logout(){
        viewModelScope.launch {
            authRepository.logout()
            googleSignInClient.signOut().await()
            _loginFlow.value = null
            _registerFlow.value = null
        }
    }

    // login/register using Google
    fun signInWithGoogle(task: Task<GoogleSignInAccount>, scope: CoroutineScope) {
        try {
            val account = task.getResult(ApiException::class.java)!!
            val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
            scope.launch {
                _registerFlow.value = Resource.Loading
                _loginFlow.value = Resource.Loading
                val authResult = authRepository.firebaseSignInWithGoogle(credential)
                _registerFlow.value = authResult
                _loginFlow.value = authResult
            }
        } catch (e: ApiException) {
            _registerFlow.value = Resource.Failure(e)
            _loginFlow.value = Resource.Failure(e)
        }
    }
}