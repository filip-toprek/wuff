package com.filiptoprek.wuff.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.filiptoprek.wuff.R
import com.filiptoprek.wuff.domain.model.location.Location
import com.filiptoprek.wuff.domain.model.location.LocationPoint
import com.filiptoprek.wuff.domain.repository.auth.AuthRepository
import com.filiptoprek.wuff.domain.repository.location.LocationClient
import com.filiptoprek.wuff.domain.repository.location.LocationRepository
import com.filiptoprek.wuff.domain.repository.profile.ProfileRepository
import com.filiptoprek.wuff.domain.repository.reservation.ReservationRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class LocationService: Service() {
    private var isRunning = false

    @Inject
    lateinit var locationClient: LocationClient

    @Inject
    lateinit var locationRepository: LocationRepository

    @Inject
    lateinit var authRepository: AuthRepository

    @Inject
    lateinit var profileRepository: ProfileRepository

    @Inject
    lateinit var reservationRepository: ReservationRepository

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action){
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start()
    {
        if(authRepository.currentUser == null || isRunning)
            return

        isRunning = true

        val notification = NotificationCompat.Builder(this, "location")
            .setContentTitle("Praćenje lokacije...")
            .setSmallIcon(R.drawable.dog)
            .setOngoing(true)

        //val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        locationClient.getLocationUpdates(10000L)
            .catch {e ->
                e.printStackTrace()
            }
            .onEach {location->
                sendLocationToBackend(Location(location.latitude, location.longitude, authRepository.currentUser?.uid.toString()))
            }
            .launchIn(serviceScope)

        startForeground(1,notification.build())
    }

    private fun stop()
    {
        isRunning = false
        stopForeground(true)
        stopSelf()
    }

    private suspend fun sendLocationToBackend(location: Location) {
        locationRepository.sendLocation(location)
        if(profileRepository.getUserProfile(authRepository.currentUser?.uid.toString())?.walker?.approved == true)
        {
            locationRepository.sendWalkLocationPoint(LocationPoint(
                location = location,
                reservationId = reservationRepository.getStartedReservationByWalkerId(authRepository.currentUser?.uid.toString()).reservationId
            ))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }

}