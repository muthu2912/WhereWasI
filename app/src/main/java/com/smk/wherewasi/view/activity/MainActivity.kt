package com.smk.wherewasi.view.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.navigation.NavigationView
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import com.smk.wherewasi.R
import com.smk.wherewasi.service.LocationForegroundService
import com.smk.wherewasi.view.fragment.PlacesVisitedFragment
import com.smk.wherewasi.viewmodel.MainViewModel

class MainActivity : AppCompatActivity(), OnNavigationItemSelectedListener {

    private lateinit var viewModel: MainViewModel
    private lateinit var serviceIntent: Intent
    private lateinit var navigationView: NavigationView
    private lateinit var currentUserTextView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        setSupportActionBar(findViewById(R.id.toolbar))
        setNavigationDrawer()
        //initViews()
        //setCurrentUserProfile()
        setObservers()
        getPermissions()
        setDefaultFragment()

    }

    private fun setNavigationDrawer() {
        navigationView = findViewById(R.id.nav_view)
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this,
            drawer,
            findViewById(R.id.toolbar),
            R.string.nav_open_drawer,
            R.string.nav_close_drawer
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == R.id.nav_places_visited) {
            setDefaultFragment()
        }
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun setCurrentUserProfile() {
        currentUserTextView.text = viewModel.loggedInUser.value
    }

    private fun setObservers() {
        observeCurrentUser()
    }

    private fun observeCurrentUser() {
        viewModel.loggedInUser.observe(this) { result ->
            if (result != null) {
                //currentUserTextView.text = result
            }
        }
    }

    private fun setDefaultFragment() {
        val fragment = PlacesVisitedFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.content_frame, fragment)
            .commit()
    }
    private fun startLocationService() {
        serviceIntent = Intent(this, LocationForegroundService::class.java)
        ContextCompat.startForegroundService(this,serviceIntent)
    }
    private fun getPermissions(){
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request location permissions
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }else{
            startLocationService()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permissions granted
                Toast.makeText(this, "Granted", Toast.LENGTH_SHORT).show()
                startLocationService()
            } else {
                // Location permissions denied
                Toast.makeText(this, "Location permission required to access location updates", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initViews(){
        currentUserTextView = findViewById(R.id.user_name)
    }
    companion object{
        private const val LOCATION_PERMISSION_REQUEST_CODE = 103
    }
}