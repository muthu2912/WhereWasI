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
import com.smk.wherewasi.R
import com.smk.wherewasi.model.Location
import com.smk.wherewasi.model.MyRealm
import com.smk.wherewasi.model.MyRealm.Companion.realm
import com.smk.wherewasi.model.User
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

class LocationForegroundService: Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private val real = MyRealm.realm
    private lateinit var currentUser : User

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.Builder( LocationRequest.PRIORITY_HIGH_ACCURACY,5000).build()
        locationCallback = object : LocationCallback(){
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                for(location in locationResult.locations){
                    real.writeBlocking {
                        val currentLocationData= Location().apply {
                            val current = LocalDateTime.now()
                            val date = Date.from(current.atZone(ZoneId.systemDefault()).toInstant())
                            val formatter = SimpleDateFormat("h:mm a")
                            val formattedTime = formatter.format(date)
                            currentUser = MyRealm.getLoggedInUser()!!
                            user = currentUser.userName
                            longitude = location.longitude
                            latitude = location.latitude
                            time = formattedTime
                            Log.d("Location Data","$user | $latitude | $longitude | $time")
                        }
                        copyToRealm(currentLocationData, updatePolicy = UpdatePolicy.ALL)
                        Log.d("Services: Location updated","${location.latitude} | ${location.longitude}")
                    }
                }
                //debug() //TODO: remove this
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
        val notificationManager = getSystemService(NotificationManager::class.java) as NotificationManager
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(CHANNEL_ID, "Location Updates", importance).apply {
            lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        }
        notificationManager.createNotificationChannel(channel)

        val stopIntent = Intent(this, LocationForegroundService::class.java).apply {
            action = ACTION_STOP_FOREGROUND_SERVICE
        }
        val stopPendingIntent: PendingIntent =
            PendingIntent.getService(this, 0, stopIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT)
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
        stopForeground(true)
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun debug(){
        val locations: RealmResults<Location> = realm.query<Location>(
            "user=$0", "muthu"
        ).find()
        for(loc in locations){
            Log.d("debug loc",loc.latitude.toString())
        }
    }
    companion object {
        private const val FOREGROUND_SERVICE_ID = 101
        private const val CHANNEL_ID = "LocationForegroundServiceChannel"
        private const val ACTION_STOP_FOREGROUND_SERVICE = "stopForegroundService"
    }
}