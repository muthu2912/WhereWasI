package com.smk.wherewasi.view.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.smk.wherewasi.R
import com.smk.wherewasi.adapter.CustomMapInfoAdapter
import com.smk.wherewasi.model.Location
import com.smk.wherewasi.viewmodel.MapViewModel

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var viewModel: MapViewModel
    private lateinit var map: GoogleMap
    private lateinit var playButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG,"onCreate")
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[MapViewModel::class.java]
        arguments?.let {
            viewModel.currentLatLng = LatLng(it.getDouble("lat"), it.getDouble("lon"))
            viewModel.currentLocInfo = "${it.getString("user")},\nYou were here on\n ${it.getString("infoTime")}"
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG,"onCreateView")
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG,"onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        initViews()
        setBtnListeners()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.d(TAG,"onMapReady")
        map = googleMap


        // Move camera to received location
        val cameraPosition =
            CameraUpdateFactory.newLatLngZoom(viewModel.currentLatLng, DEFAULT_ZOOM)
        map.animateCamera(cameraPosition)

        // Add marker with information (optional)
        val marker = MarkerOptions().position(viewModel.currentLatLng)
        marker.visible(true)
        marker.title(viewModel.currentLocInfo)
        map.setInfoWindowAdapter(CustomMapInfoAdapter(requireContext()))
        map.addMarker(marker)

    }

    private fun animateToLocations() {
        var index = 0
        fun animateNext() {
            if (index < viewModel.locationHistory.size) {
                val location = viewModel.locationHistory[index]
                viewModel.currentLocInfo = "${location.user},\nYou were here on\n ${location.time}"
                val nextLatLng = LatLng(
                    location.latitude,
                    location.longitude
                )
                map.uiSettings.setAllGesturesEnabled(false)
                map.clear()
                val marker = MarkerOptions().position(nextLatLng)
                marker.title(viewModel.currentLocInfo)
                map.addMarker(marker)
                map.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(nextLatLng, DEFAULT_ZOOM),
                    PLAYBACK_DELAY,
                    object : GoogleMap.CancelableCallback {
                        override fun onFinish() {
                            index++
                            map.uiSettings.setAllGesturesEnabled(true)
                            if (viewModel.isPlayingHistory) {
                                Handler(Looper.getMainLooper()).postDelayed({
                                    animateNext()
                                }, CAMERA_ANIMATION_DELAY.toLong())
                            }
                        }
                        override fun onCancel() {
                            // Handle cancellation if needed
                        }
                    })
            }else{
                stopPlayback()
            }
        }
        animateNext()
    }

    private fun initViews() {
        playButton = view?.findViewById(R.id.btn_map_animate)!!
    }

    private fun setBtnListeners() {
        playButton.setOnClickListener {
            if (playButton.text == resources.getString(R.string.btn_text_map_play)) {
                startPlayback()
            } else {
                stopPlayback()
            }
        }
    }

    private fun startPlayback(){
        viewModel.updateLocationHistory()
        viewModel.isPlayingHistory = true
        animateToLocations()
        playButton.text = resources.getString(R.string.btn_text_map_stop)
        Toast.makeText(context, "Playback started", Toast.LENGTH_SHORT).show()
    }

    private fun stopPlayback(){
        playButton.text = resources.getString(R.string.btn_text_map_play)
        viewModel.isPlayingHistory = false
        Toast.makeText(context, "Playback stopped", Toast.LENGTH_SHORT).show()
    }


    companion object {
        private const val TAG = "Map Fragment"
        private const val CAMERA_ANIMATION_DELAY = 2000
        private const val PLAYBACK_DELAY = 1000
        private const val DEFAULT_ZOOM = 18f
        fun newInstance(location: Location): MapFragment {
            val fragment = MapFragment()
            val args = Bundle()
            args.putDouble("lat", location.latitude)
            args.putDouble("lon", location.longitude)
            args.putString("infoTime", location.time)
            args.putString("user", location.user)
            fragment.arguments = args
            return fragment
        }
    }
}
