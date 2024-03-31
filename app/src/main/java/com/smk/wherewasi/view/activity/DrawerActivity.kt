package com.smk.wherewasi.view.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.descriptionText
import com.mikepenz.materialdrawer.model.interfaces.nameRes
import com.mikepenz.materialdrawer.model.interfaces.nameText
import com.mikepenz.materialdrawer.model.interfaces.withIcon
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

    private fun getPermissions() {
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
        } else {
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
                Toast.makeText(
                    this,
                    "Location permission required to access location updates",
                    Toast.LENGTH_SHORT
                ).show()
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

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        actionBarDrawerToggle.syncState()
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
        // actionBarDrawerToggle.syncState()
    }

    private fun initDrawerHeader(savedInstanceState: Bundle?){
        headerView = AccountHeaderView(this).apply {
            attachToSliderView(slider)
            val profiles = viewModel.getProfiles()
            for (i in profiles.indices) {
                addProfile(profiles[i], i)
            }
            withSavedInstance(savedInstanceState)
            onAccountHeaderListener = {view, profile, current ->
                profile.identifier
                MyRealm.deleteLoggedInUser()
                viewModel.setLoggedInUser(profile.identifier.toInt())
                setDefaultFragment()
                Log.d("Drawer Activity","${profile.name}")
                false
            }
        }
    }


    private fun initDrawerItems(savedInstanceState: Bundle?) {
        slider.apply {
            PrimaryDrawerItem().apply { nameRes = R.string.nav_logout; identifier = 1 }
            onDrawerItemClickListener = { view, drawerItem, _ ->
                if (drawerItem.identifier == 1L) {
                    //TODO: move to loginpage
                }
                false
            }
            setSavedInstance(savedInstanceState)

        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 103
    }
}