package com.filiptoprek.wuff.presentation.location

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Interpolator
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavHostController
import com.filiptoprek.wuff.R
import com.filiptoprek.wuff.domain.model.auth.Resource
import com.filiptoprek.wuff.domain.model.location.animateMarker
import com.filiptoprek.wuff.presentation.home.AppTitle
import com.filiptoprek.wuff.presentation.shared.SharedViewModel
import com.filiptoprek.wuff.ui.theme.Pattaya
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun LocationScreen(
    navController: NavHostController,
    locationViewModel: LocationViewModel,
    sharedViewModel: SharedViewModel
)
{
    var isSuccess = remember {
        mutableStateOf(false)
    }
    val locationFlow = locationViewModel.locationFlow.collectAsState()
    val cameraState = rememberCameraPositionState()
    val markerState = rememberMarkerState(position= LatLng(locationViewModel.location.value?.latitude ?: 45.55111, locationViewModel.location.value?.longitude ?: 18.69389))

    Column(
        modifier = Modifier
            .background(colorResource(R.color.background_white))
            .fillMaxSize()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .wrapContentHeight(Alignment.Top)
    ) {
        AppTitle()
        Spacer(modifier = Modifier.size(20.dp))

        locationFlow.value?.let {
            when(it){
                is Resource.Failure -> {
                    navController.popBackStack()
                    isSuccess.value = false;
                }
                Resource.Loading -> {
                }
                is Resource.Success -> {
                    isSuccess.value = true;
                }
            }
        }

        if(isSuccess.value)
        {
            val lifecycleOwner = LocalLifecycleOwner.current

            DisposableEffect(lifecycleOwner) {
                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_PAUSE) {
                        locationViewModel.stopLocationUpdates()
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)

                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                    locationViewModel.stopLocationUpdates()
                }
            }

            locationViewModel.location.observeForever { location ->
                val endLocation = LatLng(location.latitude, location.longitude)
                //markerState.position = endLocation

                CoroutineScope(Dispatchers.Main).launch {
                    cameraState.animate(
                        update = CameraUpdateFactory.newCameraPosition(
                            CameraPosition(markerState.position, 18F, 0f, 0f)
                        ),
                        durationMs = 1000
                    )
                    animateMarker(endLocation, markerState)
                }
            }
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraState,
                properties = MapProperties(
                    isMyLocationEnabled = false,
                    mapType = MapType.NORMAL,
                    isTrafficEnabled = false,
                    minZoomPreference = 14.0f
                )
            ) {
                Marker(
                    state = markerState,
                    title = "Šetač ${sharedViewModel.selectedReservation?.walker?.user?.name!!.split(" ")[0]}",
                    snippet = "Ovdje se trenutno nalazi vaš šetač",
                    draggable = false,
                    flat = true,
                    icon = bitmapDescriptorFromVector(LocalContext.current, R.drawable.marker)
                )
            }
        }
    }
}

private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
    return ContextCompat.getDrawable(context, vectorResId)?.run {
        setBounds(0, 0, intrinsicWidth, intrinsicHeight)
        val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
        draw(Canvas(bitmap))
        BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}