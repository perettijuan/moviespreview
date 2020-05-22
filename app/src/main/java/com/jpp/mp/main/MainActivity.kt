package com.jpp.mp.main

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.transition.ChangeBounds
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
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.navigateUp
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.jpp.mp.R
import com.jpp.mp.common.extensions.withViewModel
import com.jpp.mp.common.navigation.NavigationViewModel
import com.jpp.mpdesign.ext.closeDrawerIfOpen
import com.jpp.mpdesign.ext.setGone
import com.jpp.mpdesign.ext.setVisible
import com.jpp.mpsearch.SearchActivity
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_main.*
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
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var mpToolbarManager: MPToolbarManager

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Important to avoid blinks in transitions.
        // Source -> https://stackoverflow.com/questions/28364106/blinking-screen-on-image-transition-between-activities
        window.exitTransition = null

        mpToolbarManager = MPToolbarManager()

        withMainViewModel {
            onInit()

            viewState.observe(this@MainActivity, Observer { viewState -> renderViewState(viewState) })

            moduleNavEvents.observe(this@MainActivity, Observer {
                it.actionIfNotHandled { navEvent ->
                    when (navEvent) {
                        is ModuleNavigationEvent.NavigateToNodeWithDirections -> innerNavigateTo(navEvent.directions)
                        is ModuleNavigationEvent.NavigateToNodeWithId -> interModuleNavigationTo(navEvent.nodeId)
                        is ModuleNavigationEvent.NavigateToNodeWithExtras -> navigateToModuleWithExtras(navEvent.nodeId, navEvent.extras)
                        is ModuleNavigationEvent.NavigateToPrevious -> withNavController { popBackStack() }
                    }
                }
            })
        }

        withNavigationViewModel {
            navEvents.observe(this@MainActivity, Observer { it.actionIfNotHandled { destination -> withMainViewModel { onRequestToNavigateToDestination(destination) } } })
            reachedDestinations.observe(this@MainActivity, Observer { withMainViewModel { onDestinationReached(it) } })
        }

        appBarConfiguration = AppBarConfiguration(setOf(
                R.id.playingMoviesFragment,
                R.id.popularMoviesFragment,
                R.id.upcomingMoviesFragment,
                R.id.topRatedMoviesFragment),
                mainDrawerLayout)

        setSupportActionBar(mainToolbar)
        setupNavigation()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navigateUp(findNavController(this, R.id.mainNavHostFragment), appBarConfiguration)
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
                menu.getItem(i).isVisible = viewState.value?.menuBarEnabled ?: true
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
                interModuleNavigationTo(R.id.about_nav)
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
        findNavController(this, R.id.mainNavHostFragment).let { navController ->
            /*
             * We want several top-level destinations since we're showing the
             * navigation drawer.
             */
            setupActionBarWithNavController(
                    this,
                    navController,
                    appBarConfiguration
            )
            setupWithNavController(mainNavigationView, navController)
        }
    }

    private fun interModuleNavigationTo(@IdRes resId: Int) {
        withNavController {
            navigate(resId, null, buildAnimationNavOptions())
        }
    }

    private fun innerNavigateTo(directions: NavDirections) {
        withNavController { navigate(directions) }
    }

    private fun withNavController(action: NavController.() -> Unit) {
        findNavController(this, R.id.mainNavHostFragment).action()
    }

    private fun navigateToModuleWithExtras(@IdRes moduleNavId: Int, extras: Bundle) {
        withNavController {
            navigate(moduleNavId,
                    extras,
                    buildAnimationNavOptions()
            )
        }
    }

    private fun navigateToSearch() {
//        withNavController {
//            navigate(
//                    object : NavDirections {
//                        override fun getArguments() = Bundle()
//                        override fun getActionId() = R.id.search_nav
//                    },
//                    buildAnimationNavOptions())
//        }
        val intent = Intent(this, SearchActivity::class.java)
        val transitionOptions = ActivityOptions
                .makeSceneTransitionAnimation(this, mainToolbar, getString(R.string.toolbar_search_transition))
                .toBundle()
        startActivity(intent, transitionOptions)
    }

    private fun buildAnimationNavOptions() = NavOptions.Builder()
            .setEnterAnim(R.anim.fragment_enter_slide_right)
            .setExitAnim(R.anim.fragment_exit_slide_right)
            .setPopEnterAnim(R.anim.fragment_enter_slide_left)
            .setPopExitAnim(R.anim.fragment_exit_slide_left)
            .build()

    private fun withMainViewModel(action: MainActivityViewModel.() -> Unit) = withViewModel<MainActivityViewModel>(viewModelFactory) { action() }
    private fun withNavigationViewModel(action: NavigationViewModel.() -> Unit) = withViewModel<NavigationViewModel>(viewModelFactory) { action() }

    private fun renderViewState(viewState: MainActivityViewState) {
        supportActionBar?.title = viewState.sectionTitle
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
     * space to provide more space for the contentViewState shown right next
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
