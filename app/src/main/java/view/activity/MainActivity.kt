package view.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.navigation.NavigationView
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import com.smk.wherewasi.R
import view.fragment.PlacesVisitedFragment
import viewmodel.MainViewModel

class MainActivity : AppCompatActivity(), OnNavigationItemSelectedListener {

    private lateinit var viewModel: MainViewModel
    private lateinit var serviceIntent: Intent
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        setSupportActionBar(findViewById(R.id.toolbar))
        setNavigationDrawer()
        setCurrentUserProfile()
        setObservers()
        setDefaultFragment()
    }

    private fun setNavigationDrawer() {
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
        findViewById<NavigationView>(R.id.nav_view).setNavigationItemSelectedListener(this)
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
        findViewById<TextView>(R.id.user_name).text = viewModel.loggedInUser.value
    }

    private fun setObservers() {
        observeCurrentUser()
    }

    private fun observeCurrentUser() {
        viewModel.loggedInUser.observe(this) { result ->
            if (result != null) {
                findViewById<TextView>(R.id.user_name).text = result
            }
        }
    }

    private fun setDefaultFragment() {
        val fragment = PlacesVisitedFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.content_frame, fragment)
            .commit()
    }
}