package com.smk.wherewasi.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.smk.wherewasi.R
import com.smk.wherewasi.model.Location
import com.smk.wherewasi.model.MyRealm
import com.smk.wherewasi.model.MyRealm.Companion.realm
import io.realm.kotlin.UpdatePolicy
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

class LocationForegroundService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private val real = realm
    private lateinit var currentUser: String

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest =LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY,
            DEFAULT_LOCATION_UPDATE_INTERVAL).build()
            //LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000).build()
        locationCallback = object : LocationCallback() {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                for (location in locationResult.locations) {
                    real.writeBlocking {
                        val currentLocationData = Location().apply {
                            val current = LocalDateTime.now()
                            val date = Date.from(current.atZone(ZoneId.systemDefault()).toInstant())
                            val formatter = SimpleDateFormat.getDateTimeInstance()
                            val formattedTime = formatter.format(date)

                            currentUser = MyRealm.getCurrentUser().toString()
                            user = currentUser
                            //Making change to locations to simulate movement
                            longitude = location.longitude + Math.random() * 0.001
                            latitude = location.latitude + Math.random() * 0.001
                            time = formattedTime
                            Log.d("Location Data", "$user | $latitude | $longitude | $time")
                        }
                        copyToRealm(currentLocationData, updatePolicy = UpdatePolicy.ALL)
                        Log.d(
                            "Services: Location updated",
                            "${location.latitude} | ${location.longitude}"
                        )
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return when (intent?.action) {
            ACTION_STOP_FOREGROUND_SERVICE -> {
                stopSelf()
                START_NOT_STICKY
            }

            else -> {
                val notification = createNotification()
                startForeground(FOREGROUND_SERVICE_ID, notification)
                requestLocationUpdates()
                START_STICKY
            }
        }
    }

    private fun requestLocationUpdates() {
        // Check for location permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            // Request location updates
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotification(): Notification {
        val notificationManager =
            getSystemService(NotificationManager::class.java) as NotificationManager
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(CHANNEL_ID, "Location Updates", importance).apply {
            lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        }
        notificationManager.createNotificationChannel(channel)

        val stopIntent = Intent(this, LocationForegroundService::class.java).apply {
            action = ACTION_STOP_FOREGROUND_SERVICE
        }
        val stopPendingIntent: PendingIntent =
            PendingIntent.getService(
                this, 0, stopIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
            )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Location Updates")
            .setContentText("Getting location updates every 15 minutes")
            .setSmallIcon(R.drawable.ic_location_pin)
            .addAction(R.drawable.ic_stop, "Stop", stopPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null
    override fun onDestroy() {
        super.onDestroy()
        stopForeground(STOP_FOREGROUND_REMOVE)
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    companion object {
        private const val FOREGROUND_SERVICE_ID = 101
        private const val CHANNEL_ID = "LocationForegroundServiceChannel"
        private const val ACTION_STOP_FOREGROUND_SERVICE = "stopForegroundService"
        private const val DEFAULT_LOCATION_UPDATE_INTERVAL = 5000L
    }
}