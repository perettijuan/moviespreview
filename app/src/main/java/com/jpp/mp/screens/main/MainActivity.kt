package com.jpp.mp.screens.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.*
import com.jpp.mp.R
import com.jpp.mp.common.extensions.getStringOrDefault
import com.jpp.mp.common.extensions.navigate
import com.jpp.mp.common.navigation.Destination
import com.jpp.mp.common.navigation.Destination.*
import com.jpp.mp.common.navigation.NavigationViewModel
import com.jpp.mp.ext.closeDrawerIfOpen
import com.jpp.mp.ext.setActionBarTitle
import com.jpp.mp.ext.withViewModel
import com.jpp.mpdesign.ext.setGone
import com.jpp.mpdesign.ext.setVisible
import com.jpp.mpmoviedetails.NavigationMovieDetails
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


/**
 * Main entry point of the appModule.
 *
 * Navigation: shows a DrawerLayout with the different items that the user can select in order
 * to be redirected to a particular destination of the appModule. Uses Navigation Component
 * in order to delegate the navigation logic to the Architecture Components.
 *
 * Multi-top-level fragments navigation: the navigation guidelines described in the Android
 * support site states that the appModule should have only one entry point, meaning only
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
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var mpToolbarManager: MPToolbarManager

    private val topLevelDestinations = listOf(
            R.id.playingMoviesFragment,
            R.id.popularMoviesFragment,
            R.id.upcomingMoviesFragment,
            R.id.topRatedMoviesFragment
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mpToolbarManager = MPToolbarManager()

        withMainViewModel {
            onInit()
            viewState().observe(this@MainActivity, Observer { viewState ->
                renderViewState(viewState)
            })
        }

        withNavigationViewModel {
            navEvents.observe(this@MainActivity, Observer { it.actionIfNotHandled { destination -> navigateToDestination(destination) } })
            reachedDestinations.observe(this@MainActivity, Observer { onDestinationReached(it) })
        }

        setSupportActionBar(mainToolbar)
        setupNavigation()
    }

    override fun onSupportNavigateUp(): Boolean {
        /*
         * 12/26/2018 - Navigation Library 1.0.0-alpha07
         * 01/18/2019 - Navigation Library 1.0.0-alpha09
         *
         * Manage inner navigation: since we have multiple home destinations (all the fragments that
         * are sub-classes of MoviesFragment) we need to manage the ActionBar button click for some cases (the
         * burger or the arrow). If we fail to do so, the navigation library is assuming we're trying to
         * expanded the navigate drawer from the current destination.
         * The logic here determinate if the current destination is one of the non-home destination fragments
         * (the ones that are deeper in the navigation structure) and if it is the case asks the navigate controller
         * to navigate one step up.
         * If it is a home destination, it opens the drawer and asks the navigate controller to manage the navigation
         * as usual.
         */
        if (topLevelDestinations.contains(findNavController(this, R.id.mainNavHostFragment).currentDestination?.id)) {
            if (!mainDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                // this ensures that we support multiple top level fragments
                mainDrawerLayout.openDrawer(GravityCompat.START)
                return true
            }
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_activity_menu, menu)

        /*
         * Disable the menu if the current view state requires
         * to hide the menus - this is true when we're showing
         * a non-top-level fragment.
         */
        withMainViewModel {
            for (i in 0 until menu.size()) {
                menu.getItem(i).isVisible = viewState().value?.menuBarEnabled ?: true
            }
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search_menu -> {
                navigateToSearch()
                return true
            }
            R.id.about_menu -> {
                interModuleNavigationTo(R.id.aboutFragment)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = fragmentDispatchingAndroidInjector

    /**
     * Prepare the navigation components by setting the fragments that are going to be used
     * as top level destinations of the drawer and adding a navigation listener to update
     * the view state that the ViewModel is showing.
     */
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
         * Make sure we update the ViewModel every time a new destination
         * is selected.
         */
        navController.addOnDestinationChangedListener { _, destination, arguments ->
            when (destination.id) {
                R.id.playingMoviesFragment -> withMainViewModel { userNavigatesToMovieListSection(destination.label.toString()) }
                R.id.popularMoviesFragment -> withMainViewModel { userNavigatesToMovieListSection(destination.label.toString()) }
                R.id.upcomingMoviesFragment -> withMainViewModel { userNavigatesToMovieListSection(destination.label.toString()) }
                R.id.topRatedMoviesFragment -> withMainViewModel { userNavigatesToMovieListSection(destination.label.toString()) }
                R.id.personFragment -> withMainViewModel { userNavigatesToPerson(arguments.getStringOrDefault("personName", destination.label.toString())) }
                R.id.creditsFragment -> withMainViewModel { userNavigatesToCredits(arguments.getStringOrDefault("movieTitle", destination.label.toString())) }
                R.id.aboutFragment -> withMainViewModel { userNavigatesToAbout(getString(R.string.about_top_bar_title)) }
                R.id.licensesFragment -> withMainViewModel { userNavigatesToLicenses(getString(R.string.about_open_source_action)) }
                R.id.licenseContentFragment -> withMainViewModel { userNavigatesToLicenseContent(arguments.getStringOrDefault("licenseTitle", destination.label.toString())) }
            }
        }

        setupWithNavController(mainNavigationView, navController)
    }

    private fun navigateToDestination(destination: Destination) {
        when (destination) {
            is MPAccount -> interModuleNavigationTo(R.id.user_account_nav)
            is MPMovieDetails -> {
                withNavController {
                    navigate(R.id.movie_details_nav,
                            NavigationMovieDetails.navArgs(destination.movieId, destination.movieImageUrl, destination.movieTitle, destination.transitionView.transitionName),
                            FragmentNavigatorExtras(destination.transitionView to destination.transitionView.transitionName))
                }
            }
            is PreviousDestination -> withNavController { popBackStack() }
            is InnerDestination -> innerNavigateTo(destination.directions)
        }
    }

    private fun onDestinationReached(reachedDestination: Destination) {
        when (reachedDestination) {
            is ReachedDestination -> withMainViewModel { userNavigatesWithinFeature(reachedDestination.destinationTitle) }
            is MPSearch -> withMainViewModel { userNavigatesToSearch() }
        }
    }

    private fun interModuleNavigationTo(@IdRes resId: Int) {
        withNavController { navigate(resId) }
    }

    private fun innerNavigateTo(directions: NavDirections) {
        withNavController { navigate(directions) }
    }

    private fun withNavController(action: NavController.() -> Unit) {
        findNavController(this, R.id.mainNavHostFragment).action()
    }

    private fun navigateToSearch() {
        withNavController {
            navigate(
                    object : NavDirections {
                        override fun getArguments() = Bundle()
                        override fun getActionId() = R.id.search_nav
                    },
                    NavOptions.Builder()
                            .setEnterAnim(R.anim.fragment_enter_slide_right)
                            .setExitAnim(R.anim.fragment_exit_slide_right)
                            .setPopEnterAnim(R.anim.fragment_enter_slide_left)
                            .setPopExitAnim(R.anim.fragment_exit_slide_left)
                            .build())
        }
    }


    private fun withMainViewModel(action: MainActivityViewModel.() -> Unit) = withViewModel<MainActivityViewModel>(viewModelFactory) { action() }
    private fun withNavigationViewModel(action: NavigationViewModel.() -> Unit) = withViewModel<NavigationViewModel>(viewModelFactory) { action() }

    private fun renderViewState(viewState: MainActivityViewState) {
        setActionBarTitle(viewState.sectionTitle)
        when (viewState.searchEnabled) {
            true -> {
                mainSearchView.setVisible()
                mpToolbarManager.setInsetStartWithNavigation(0, mainToolbar)
            }
            false -> {
                mainSearchView.setGone()
                mpToolbarManager.clearInsetStartWithNavigation(mainToolbar)
            }
        }

        /*
         * Forces to inflate the menu shown in the Toolbar.
         * onCreateOptionsMenu() will be re-executed to hide/show the
         * menu options
         */
        invalidateOptionsMenu()
        mainDrawerLayout.closeDrawerIfOpen()
    }

    /**
     * Helper class to remove the space between the arrow image and the
     * text (or the SearchView) that is shown in the Toolbar.
     * In some screens (e.g.: searchPage screen) we want to remove this
     * space to provide more space for the content shown right next
     * to the arrow/burger icon.
     */
    private inner class MPToolbarManager {
        private var originalInsetStartWithNavigation = -1


        fun setInsetStartWithNavigation(toSet: Int, toolbar: Toolbar) {
            originalInsetStartWithNavigation = toSet
            toolbar.contentInsetStartWithNavigation = toSet
        }


        fun clearInsetStartWithNavigation(toolbar: Toolbar) {
            when (originalInsetStartWithNavigation != -1) {
                true -> {
                    toolbar.contentInsetStartWithNavigation = originalInsetStartWithNavigation
                    originalInsetStartWithNavigation = -1
                }
            }
        }

    }
}