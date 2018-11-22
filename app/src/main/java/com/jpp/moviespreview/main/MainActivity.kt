package com.jpp.moviespreview.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.*
import com.jpp.moviespreview.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(mainToolbar)
        setupNavigation()
    }

    override fun onSupportNavigateUp(): Boolean {
        // ensures that the menu items in the Nav Drawer stay in sync with the navigation graph
        return navigateUp(mainDrawerLayout, findNavController(this, R.id.mainNavHostFragment))
    }

    override fun onBackPressed() {
        if (mainDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mainDrawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


    private fun setupNavigation() {
        val navController = findNavController(this, R.id.mainNavHostFragment)

        val appBarConfiguration = AppBarConfiguration(setOf(R.id.playingMoviesFragment, R.id.popularMoviesFragment), mainDrawerLayout)
        setupActionBarWithNavController(this, navController, appBarConfiguration)

        // handle nav drawer item clicks
        mainNavigationView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            mainDrawerLayout.closeDrawers()
            true
        }

        setupWithNavController(mainNavigationView, navController)
    }
}