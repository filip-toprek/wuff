package com.filiptoprek.wuff.presentation.location

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import com.filiptoprek.wuff.domain.model.location.Location
import com.filiptoprek.wuff.presentation.shared.SharedViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Interpolator
import android.os.Bundle
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.filiptoprek.wuff.R
import com.filiptoprek.wuff.domain.model.auth.Resource
import com.filiptoprek.wuff.domain.model.location.LocationPoint
import com.filiptoprek.wuff.domain.model.location.animateMarker
import com.filiptoprek.wuff.ui.theme.Pattaya
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
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
fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context)
    }

    val lifecycleObserver = rememberMapLifecycleObserver(mapView)
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycle.removeObserver(lifecycleObserver)
        }
    }

    return mapView
}

@Composable
fun rememberMapLifecycleObserver(mapView: MapView): LifecycleEventObserver =
    remember(mapView) {
        LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> mapView.onCreate(Bundle())
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> throw IllegalStateException()
            }
        }
    }
@Composable
fun LocationPathScreen(locationViewModel: LocationViewModel, sharedViewModel: SharedViewModel) {
    val locationPoints by locationViewModel.locationPoints.observeAsState(emptyList())
    val mapView = rememberMapViewWithLifecycle()
    val pathColor = colorResource(R.color.green_accent)
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        locationViewModel.fetchLocationPoints(sharedViewModel.selectedReservation?.walkerUserId.toString(), sharedViewModel.selectedReservation?.reservationId.toString())
    }

    AndroidView({mapView}){ map ->
        coroutineScope.launch {
            map.getMapAsync { googleMap ->
                    googleMap.uiSettings.isZoomControlsEnabled = true
                    drawPathOnMap(googleMap, locationPoints, pathColor.toArgb(), locationViewModel, sharedViewModel)
                }
            }
        }
    }
@SuppressLint("MissingPermission")
fun drawPathOnMap(googleMap: GoogleMap, locationPoints: List<Location>, pathColor: Int, locationViewModel: LocationViewModel, sharedViewModel: SharedViewModel) {
    if (locationPoints.isNotEmpty()) {
    locationPoints as List<HashMap<String, Double>>
        val polylineOptions = PolylineOptions()
            .color(pathColor)
            .width(5f)
            .geodesic(true)

        // Add each location point to the polyline
        locationPoints.forEach { point ->
            val latitude = point["latitude"] ?: return@forEach
            val longitude = point["longitude"] ?: return@forEach
            polylineOptions.add(LatLng(latitude, longitude))
        }

        // Add polyline to map
        googleMap.addPolyline(polylineOptions)

        // Optionally, move camera to the first point
        val firstPoint = locationPoints.firstOrNull()
        firstPoint?.let {
            val latitude = it["latitude"] ?: return@let
            val longitude = it["longitude"] ?: return@let
            val cameraPosition = CameraPosition.Builder()
                .target(LatLng(latitude, longitude))
                .zoom(15f)
                .build()
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }
        locationViewModel.calculatePolylineDistance(polylineOptions.points, sharedViewModel.selectedReservation?.reservationId.toString())
    }
}