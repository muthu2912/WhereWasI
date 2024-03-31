package com.smk.wherewasi.viewmodel

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.smk.wherewasi.model.Location
import com.smk.wherewasi.model.MyRealm
import io.realm.kotlin.ext.query

class MapViewModel : ViewModel() {
    private val realm = MyRealm.realm

    lateinit var locationHistory: List<Location>
    lateinit var currentLatLng: LatLng
    var currentLocInfo = ""
    var isPlayingHistory = false;

    init {
        updateLocationHistory()
    }

    fun updateLocationHistory() {
        locationHistory = realm.query<Location>(
            "user=$0", MyRealm.getLoggedInUser()
        ).find().reversed()
    }
}