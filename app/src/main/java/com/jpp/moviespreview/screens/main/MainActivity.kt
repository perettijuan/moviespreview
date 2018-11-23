package com.jpp.moviespreview.screens.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.*
import com.jpp.moviespreview.R
import com.jpp.moviespreview.ext.setGone
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Main entry point of the application.
 *
 * Navigation: shows a DrawerLayout with the different items that the user can select in order
 * to be redirected to a particular destination of the application. Uses Navigation Component
 * in order to delegate the navigation logic to the Architecture Components.
 *
 * Multi-top-level fragments navigation: the navigation guidelines described in the Android
 * support site states that the application should have only one entry point, meaning only
 * one top-level fragment per navigation graph. Now, when we're using a DrawerLayout (the menu
 * with the 'burger' icon), we need more than one top-level fragment, since we want to
 * show the burger icon in every fragment that the user can select from the drawer.
 * In order to achieve this, we can use a flavor of [NavigationUi.setupActionBarWithNavController()]
 * that receives a [AppBarConfiguration] instance with the list of top level destinations. Now, this
 * is not enough to have that support enabled. It is VERY IMPORTANT that we override [onSupportNavigateUp]
 * in order to control whether the NavComponent should control or not the click on the top bar
 * icon. If we fail to do this, every time the user clicks the burger icon, the NavController
 * will attempt to add the startDestination Fragment in the back stack.
 *
 *
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(mainToolbar)
        setupNavigation()
        lockActionBar()
    }

    override fun onSupportNavigateUp(): Boolean {
        if(findNavController(this, R.id.mainNavHostFragment).currentDestination?.id == R.id.searchFragment) {
            return navigateUp(findNavController(this, R.id.mainNavHostFragment), mainDrawerLayout)
        }

        if (!mainDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            // this ensures that we support multiple top level fragments
            mainDrawerLayout.openDrawer(GravityCompat.START)
            return true
        }
        // this ensures that the menu items in the Nav Drawer stay in sync with the navigation graph
        return navigateUp(findNavController(this, R.id.mainNavHostFragment), mainDrawerLayout)
    }

    override fun onBackPressed() {
        if (mainDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mainDrawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search_menu -> {
                // Probably the best idea here is to navigate to a new Activity
                findNavController(this, R.id.mainNavHostFragment).navigate(R.id.searchFragment)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    private fun setupNavigation() {
        val navController = findNavController(this, R.id.mainNavHostFragment)

        /*
         * We want several top-level destinations since we're showing the
         * navigation drawer.
         */
        setupActionBarWithNavController(this,
                navController,
                AppBarConfiguration(setOf(
                        R.id.playingMoviesFragment,
                        R.id.popularMoviesFragment,
                        R.id.upcomingMoviesFragment,
                        R.id.topRatedMoviesFragment),
                        mainDrawerLayout)
        )

        /*
         * Syncs the TopBar title whit the newly added destination.
         */
        navController.addOnNavigatedListener { _, destination ->
            supportActionBar?.title = destination.label
            mainCollapsingToolbarLayout.title = destination.label
        }

        setupWithNavController(mainNavigationView, navController)
    }


    /**
     * Disable the CollapsingToolbarLayout behavior in order to show a fixed
     * top bar.
     */
    private fun lockActionBar() {
        // disable expanded mode in AppBarLayout container
        mainAppBarLayout.apply {
            setExpanded(false, false)
            isActivated = false
            val lp = layoutParams as CoordinatorLayout.LayoutParams
            lp.height = resources.getDimension(R.dimen.action_bar_height_normal).toInt()
        }
        mainCollapsingToolbarLayout.apply {
            isTitleEnabled = false
        }
        mainImageView.setGone()
    }
}