package com.filiptoprek.wuff.domain.model.location

import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator
import com.filiptoprek.wuff.domain.model.location.LatLngInterpolator.LinearFixed
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MarkerState


fun animateMarker(destination: LatLng, marker: MarkerState?) {
    if (marker != null) {
        val startPosition = marker.position
        val endPosition = LatLng(destination.latitude, destination.longitude)

        val latLngInterpolator: LatLngInterpolator = LinearFixed()
        val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
        valueAnimator.setDuration(1000) // duration 1 second
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.addUpdateListener { animation ->
            try {
                val v = animation.animatedFraction
                val newPosition = latLngInterpolator.interpolate(v, startPosition, endPosition)
                marker.position = newPosition
            } catch (ex: Exception) {
                //
            }
        }

        valueAnimator.start()
    }
}
