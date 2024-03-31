package com.smk.wherewasi.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.iconDrawable
import com.mikepenz.materialdrawer.model.interfaces.nameRes
import com.mikepenz.materialdrawer.widget.AccountHeaderView
import com.mikepenz.materialdrawer.widget.MaterialDrawerSliderView
import com.smk.wherewasi.R
import com.smk.wherewasi.model.MyRealm
import com.smk.wherewasi.service.LocationForegroundService
import com.smk.wherewasi.view.fragment.PlacesVisitedFragment
import com.smk.wherewasi.viewmodel.DrawerViewModel

class DrawerActivity : AppCompatActivity() {

    private lateinit var viewModel: DrawerViewModel
    private lateinit var slider: MaterialDrawerSliderView
    private lateinit var toolbar: Toolbar
    private lateinit var headerView: AccountHeaderView
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var serviceIntent: Intent
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawer)

        viewModel = ViewModelProvider(this)[DrawerViewModel::class.java]

        initDrawerViews()
        initDrawerItems(savedInstanceState)
        initDrawerHeader(savedInstanceState)

        getPermissions()

        setDefaultFragment()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun getPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                if (permissionsToRequest.contains(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    LOCATION_PERMISSION_REQUEST_CODE
                } else {
                    NOTIFICATION_PERMISSION_REQUEST_CODE
                }
            )
        } else {
            startLocationService() // If both permissions are already granted, proceed
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Location permission granted
                    Toast.makeText(this, "Location permission granted", Toast.LENGTH_SHORT).show()
                    getPermissions() // Check for notification permission
                } else {
                    // Location permission denied
                    Toast.makeText(this, "Location permission required", Toast.LENGTH_SHORT).show()
                }
            }
            NOTIFICATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Notification permission granted
                    Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show()
                    startLocationService()
                } else {
                    // Notification permission denied
                    Toast.makeText(this, "Notification permission required", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun startLocationService() {
        serviceIntent = Intent(this, LocationForegroundService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    private fun setDefaultFragment() {
        val fragment = PlacesVisitedFragment()

        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_container, fragment)
            .commit()
    }

    private fun startLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(serviceIntent)
    }

    private fun initDrawerViews() {
        slider = findViewById(R.id.slider)
        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.app_name)
        supportActionBar?.setHomeButtonEnabled(true)

        actionBarDrawerToggle = ActionBarDrawerToggle(
            this,
            findViewById(R.id.root),
            toolbar,
            com.mikepenz.materialdrawer.R.string.material_drawer_open,
            com.mikepenz.materialdrawer.R.string.material_drawer_close
        )

        actionBarDrawerToggle.syncState()
    }

    private fun initDrawerHeader(savedInstanceState: Bundle?) {
        headerView = AccountHeaderView(this).apply {
            val profiles = viewModel.iProfiles

            attachToSliderView(slider)
            for (i in profiles.indices) {
                addProfile(profiles[i], i)
            }

            withSavedInstance(savedInstanceState)
            onAccountHeaderListener = { _, profile, _ ->
                profile.identifier
                MyRealm.removeCurrentUser()
                viewModel.setCurrentUser(profile.identifier.toInt())
                setDefaultFragment()
                Log.d("Drawer Activity", "${profile.name}")
                false
            }
            setActiveProfile(viewModel.currentUserIndex, false)
        }

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initDrawerItems(savedInstanceState: Bundle?) {
        slider.apply {

            itemAdapter.add(
                PrimaryDrawerItem().apply {
                    nameRes = R.string.nav_logout; iconDrawable =
                    getDrawable(R.drawable.ic_logout); identifier = 1
                }
            )
            onDrawerItemClickListener = { _, drawerItem, _ ->
                if (drawerItem.identifier == 1L) {
                    MyRealm.removeCurrentUser()
                    startLoginActivity()
                }
                false
            }
            setSavedInstance(savedInstanceState)
        }
    }

    companion object {
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 102
        private const val LOCATION_PERMISSION_REQUEST_CODE = 103
    }
}