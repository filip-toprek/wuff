package com.filiptoprek.wuff

import android.Manifest
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkBuilder
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.findNavController
import com.filiptoprek.wuff.domain.model.reservation.Reservation
import com.filiptoprek.wuff.navigation.Routes
import com.filiptoprek.wuff.navigation.SetupNavGraph
import com.filiptoprek.wuff.presentation.auth.AuthViewModel
import com.filiptoprek.wuff.presentation.home.HomeViewModel
import com.filiptoprek.wuff.presentation.location.LocationViewModel
import com.filiptoprek.wuff.presentation.profile.ProfileViewModel
import com.filiptoprek.wuff.presentation.rating.RatingViewModel
import com.filiptoprek.wuff.presentation.reload.ReloadViewModel
import com.filiptoprek.wuff.presentation.reservation.ReservationViewModel
import com.filiptoprek.wuff.presentation.shared.SharedViewModel
import com.filiptoprek.wuff.ui.theme.WuffTheme
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.maps.GoogleMap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val authViewModel by viewModels<AuthViewModel>()
    private val profileViewModel by viewModels<ProfileViewModel>()
    private val homeViewModel by viewModels<HomeViewModel>()
    private val reservationViewModel by viewModels<ReservationViewModel>()
    private val reloadViewModel by viewModels<ReloadViewModel>()
    private val ratingViewModel by viewModels<RatingViewModel>()
    private val sharedViewModel by viewModels<SharedViewModel>()
    private val locationViewModel by viewModels<LocationViewModel>()

    @Inject
    lateinit var googleSignInClient: GoogleSignInClient

    @Inject
    lateinit var firestore: FirebaseFirestore

    private val reservationListeners = mutableMapOf<String, ListenerRegistration>()
    private fun sendNotification(title: String, message: String) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = System.currentTimeMillis().toInt()
        val intent = Intent(this, MainActivity::class.java)

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "location"
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.dog)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    override fun onDestroy() {
        super.onDestroy()
        // Remove all listeners to avoid memory leaks
        for (listener in reservationListeners.values) {
            listener.remove()
        }
    }

    private fun listenForReservationChanges(reservationId: String) {
        val reservationRef = firestore.collection("reservations").document(reservationId)

        val listener = reservationRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("FirestoreError", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val started = snapshot.getBoolean("started")!!
                val completed = snapshot.getBoolean("completed")!!
                if (started && !completed) {
                    sharedViewModel.selectedReservation = snapshot.toObject(Reservation::class.java)
                    sendNotification("Štenja započeta.", "Vaša šetnja je započela.")
                }
            } else {
                Log.d("FirestoreError", "Current data: null")
            }
        }

        reservationListeners[reservationId] = listener
    }

    private fun fetchUserReservations() {
        val userId = authViewModel.currentUser?.uid ?: return
        firestore.collection("reservations")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val reservationId = document.id
                    listenForReservationChanges(reservationId)
                }
            }
            .addOnFailureListener { e ->
                Log.w("FirestoreError", "Error fetching reservations", e)
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ),
            0
        )
        super.onCreate(savedInstanceState)

        fetchUserReservations()
        setContent {
            WuffTheme(authViewModel = authViewModel,
                content = {
                SetupNavGraph(
                    viewModel = authViewModel,
                    profileViewModel = profileViewModel,
                    homeViewModel = homeViewModel,
                    reservationViewModel = reservationViewModel,
                    reloadViewModel = reloadViewModel,
                    ratingViewModel = ratingViewModel,
                    sharedViewModel = sharedViewModel,
                    locationViewModel = locationViewModel,
                    googleSignInClient = googleSignInClient
                )
            }
            )
        }
    }
}