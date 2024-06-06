package com.filiptoprek.wuff.presentation.location

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.filiptoprek.wuff.R
import com.filiptoprek.wuff.domain.model.auth.Resource
import com.filiptoprek.wuff.presentation.shared.SharedViewModel
import com.filiptoprek.wuff.ui.theme.Pattaya
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
        Spacer(modifier = Modifier.size(20.dp))

        locationFlow.value?.let {
            when(it){
                is Resource.Failure -> {
                    navController.popBackStack()
                    isSuccess.value = false;
                }
                Resource.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally)
                            .wrapContentHeight(Alignment.CenterVertically),
                        color = colorResource(R.color.green_accent)
                    )
                    isSuccess.value = false;

                }
                is Resource.Success -> {
                    isSuccess.value = true;
                }
            }
        }

        if(isSuccess.value)
        {
            val cameraState = rememberCameraPositionState()
            var currentLoc = remember {
                mutableStateOf(
                    LatLng(
                        0.0,
                        0.0
                    )
                )
            }
            locationViewModel.location.observeForever { location ->
                currentLoc.value = LatLng(location.latitude, location.longitude)
                cameraState.position = CameraPosition.builder()
                    .target(currentLoc.value)
                    .zoom(18F)
                    .bearing(0F)
                    .tilt(0F)
                    .build();
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
                    state = MarkerState(position = currentLoc.value),
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