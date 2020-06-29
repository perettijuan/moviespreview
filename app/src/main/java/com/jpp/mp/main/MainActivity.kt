package com.jpp.mp.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.navigateUp
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.jpp.mp.R
import com.jpp.mp.common.viewmodel.MPGenericSavedStateViewModelFactory
import com.jpp.mpdesign.ext.closeDrawerIfOpen
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

/**
 * Main entry point of the app.
 *
 * Navigation: shows a DrawerLayout with the different items that the user can select in order
 * to be redirected to a particular destination of the application. Uses Navigation Component
 * in order to delegate the navigation logic to the Architecture Components.
 *
 * Multi-top-level fragments navigation: the navigation guidelines described in the Android
 * support site states that the app should have only one entry point, meaning only
 * one top-level fragment per navigation graph. Now, when we're using a DrawerLayout (the menu
 * with the 'burger' icon), we need more than one top-level fragment, since we want to
 * show the burger icon in every fragment that the user can select from the drawer.
 * In order to achieve this, we can use a flavor of [NavigationUi.setupActionBarWithNavController()]
 * that receives a [AppBarConfiguration] instance with the list of top level destinations. Now, this
 * is not enough to have that support enabled. It is VERY IMPORTANT that we override [onSupportNavigateUp]
 * in order to control whether the NavComponent should control or not the click on the top bar
 * icon. If we fail to do this, every time the user clicks the burger icon, the NavController
 * will attempt to add the startDestination Fragment in the back stack.
 */
class MainActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var viewModelFactory: MainActivityViewModelFactory

    @Inject
    lateinit var navigator: Navigator

    private val viewModel: MainActivityViewModel by viewModels {
        MPGenericSavedStateViewModelFactory(
            viewModelFactory,
            this
        )
    }

    private lateinit var appBarConfiguration: AppBarConfiguration

    private var mainToolbar: Toolbar? = null
    private var mainDrawerLayout: DrawerLayout? = null
    private var mainNavigationView: NavigationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Important to avoid blinks in transitions.
        // Source -> https://stackoverflow.com/questions/28364106/blinking-screen-on-image-transition-between-activities
        window.exitTransition = null

        mainToolbar = findViewById(R.id.mainToolbar)
        mainDrawerLayout = findViewById(R.id.mainDrawerLayout)
        mainNavigationView = findViewById(R.id.mainNavigationView)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.playingMoviesFragment,
                R.id.popularMoviesFragment,
                R.id.upcomingMoviesFragment,
                R.id.topRatedMoviesFragment
            ),
            mainDrawerLayout
        )

        setSupportActionBar(mainToolbar)
        setupNavigation()
        viewModel.onInit()
    }

    override fun onResume() {
        super.onResume()
        navigator.bind(findNavController(R.id.mainNavHostFragment))
        navigator.bindDelegate(MainToSearchNavigationDelegate(this, mainToolbar))
    }

    override fun onPause() {
        super.onPause()
        navigator.unBind()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navigateUp(findNavController(R.id.mainNavHostFragment), appBarConfiguration)
    }

    override fun onBackPressed() {
        val drawerOpen = mainDrawerLayout?.isDrawerOpen(GravityCompat.START) ?: false
        if (drawerOpen) {
            mainDrawerLayout?.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        mainNavigationView = null
        mainDrawerLayout = null
        mainToolbar = null
        super.onDestroy()
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> =
        fragmentDispatchingAndroidInjector

    /**
     * Prepare the navigation components by setting the fragments that are going to be used
     * as top level destinations of the drawer and adding a navigation listener to update
     * the view state that the ViewModel is showing.
     */
    private fun setupNavigation() {
        val navView = mainNavigationView ?: return
        val navController = findNavController(R.id.mainNavHostFragment)

        setupActionBarWithNavController(
            this,
            navController,
            appBarConfiguration
        )
        setupWithNavController(navView, navController)
        navController.addOnDestinationChangedListener { _, _, _ ->
            mainDrawerLayout?.closeDrawerIfOpen()
        }
    }
}
