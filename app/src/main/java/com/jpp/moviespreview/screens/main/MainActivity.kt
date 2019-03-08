package com.jpp.moviespreview.screens.main

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.*
import com.google.android.material.appbar.AppBarLayout
import com.jpp.moviespreview.R
import com.jpp.moviespreview.ext.*
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
            viewState().observe(this@MainActivity, Observer { viewState ->
                renderViewState(viewState)
            })
        }

        withSearchViewViewModel {
            searchEvents().observe(this@MainActivity, Observer { event ->
                onSearchEvent(event)
            })
        }

        setSupportActionBar(mainToolbar)
        setupNavigation()

        /*
         * CollapsingToolbarLayout does not supports custom downloadable fonts
         * - or I couldn't find a way to do it - this is the best approach
         * I found to load the fonts I want for it.
         */
        with(mainCollapsingToolbarLayout) {
            val tf = Typeface.createFromAsset(assets, "fonts/poppins.ttf")
            setCollapsedTitleTypeface(tf)
            setExpandedTitleTypeface(tf)
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        /*
         * 12/26/2018 - Navigation Library 1.0.0-alpha07
         * 01/18/2019 - Navigation Library 1.0.0-alpha09
         *
         * Manage inner navigation: since we have multiple home destinations (all the fragments that
         * are sub-classes of MoviesFragment) we need to manage the ActionBar button click for some cases (the
         * burger or the arrow). If we fail to do so, the navigation library is assuming we're trying to
         * open the nav drawer from the current destination.
         * The logic here determinate if the current destination is one of the non-home destination fragments
         * (the ones that are deeper in the navigation structure) and if it is the case asks the nav controller
         * to navigate one step up.
         * If it is a home destination, it opens the drawer and asks the nav controller to manage the navigation
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
                // Probably the best idea here is to navigate to a new Activity
                findNavController(this, R.id.mainNavHostFragment).navigate(R.id.searchFragment)
                return true
            }
            R.id.about_menu -> {
                findNavController(this, R.id.mainNavHostFragment).navigate(R.id.aboutFragment)
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
                R.id.movieDetailsFragment -> {
                    arguments?.let {
                        withMainViewModel { userNavigatesToMovieDetails(it.getString("movieTitle"), it.getString("movieImageUrl")) }
                    }
                }
                R.id.searchFragment -> withMainViewModel { userNavigatesToSearch() }
                R.id.personFragment -> withMainViewModel {
                    arguments?.let {
                        userNavigatesToPerson(it.getString("personName"))
                    }
                }
                R.id.creditsFragment -> withMainViewModel {
                    arguments?.let {
                        userNavigatesToCredits(it.getString("movieTitle"))
                    }
                }
                R.id.aboutFragment -> withMainViewModel {  userNavigatesToAbout(getString(R.string.about_top_bar_title))}
            }
        }

        setupWithNavController(mainNavigationView, navController)
    }


    private fun withMainViewModel(action: MainActivityViewModel.() -> Unit) = withViewModel<MainActivityViewModel>(viewModelFactory) { action() }
    private fun withSearchViewViewModel(action: SearchViewViewModel.() -> Unit) = withViewModel<SearchViewViewModel>(viewModelFactory) { action() }

    private fun renderViewState(viewState: MainActivityViewState) {
        when (viewState) {
            is MainActivityViewState.ActionBarLocked -> {
                if (viewState.withAnimation) lockActionBarWithAnimation() else lockActionBar()
                setActionBarTitle(viewState.sectionTitle)
                mainSearchView.setGone()
            }
            is MainActivityViewState.ActionBarUnlocked -> {
                mainImageView.clearImage()
                mainSearchView.setGone()
                unlockActionBarWithAnimation {
                    mainImageView.loadImageUrl(viewState.contentImageUrl)
                    mainCollapsingToolbarLayout.enableTitle()
                    enableCollapsingToolBarLayoutTitle(viewState.sectionTitle)
                }
                mpToolbarManager.clearInsetStartWithNavigation(mainToolbar)
            }
            is MainActivityViewState.SearchEnabled -> {
                if (viewState.withAnimation) lockActionBarWithAnimation() else lockActionBar()
                setActionBarTitle(viewState.sectionTitle)
                with(mainSearchView) {
                    isIconified = false
                    setIconifiedByDefault(false)
                    setOnQueryTextListener(QuerySubmitter { withSearchViewViewModel { search(it) } })
                    setVisible()
                    findViewById<View>(androidx.appcompat.R.id.search_close_btn).setOnClickListener {
                        withSearchViewViewModel { clearSearch() }
                    }
                }
                mpToolbarManager.setInsetStartWithNavigation(0, mainToolbar)
            }
        }

        /*
         * Forces to inflate the menu shown in the Toolbar.
         * onCreateOptionsMenu() will be re-executed to hide/show the
         * menu options
         */
        invalidateOptionsMenu()
    }

    private fun onSearchEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.ClearSearch -> {
                with(mainSearchView) {
                    setQuery("", false)
                    requestFocus()
                }
            }
            is SearchEvent.Search -> {
                with(mainSearchView) {
                    clearFocus()
                }
            }
        }
    }


    /**
     * Hides the mainCollapsingToolbarLayout title when it is fully extended and shows the
     * title when the user scrolls the content of the Activity.
     */
    private fun enableCollapsingToolBarLayoutTitle(title: String) {
        mainAppBarLayout.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {

            var show = true
            var scrollRange = -1

            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.totalScrollRange
                }

                if (scrollRange + verticalOffset == 0) {
                    mainCollapsingToolbarLayout.title = title
                    show = true
                } else if (show) {
                    mainCollapsingToolbarLayout.title = " "
                    show = false
                }
            }
        })
    }


    private fun lockActionBar() {
        // disable expanded mode in AppBarLayout container
        mainAppBarLayout.apply {
            setExpanded(false, false)
            isActivated = false
            val lp = layoutParams as CoordinatorLayout.LayoutParams
            lp.height = resources.getDimension(R.dimen.action_bar_height_normal).toInt()
        }
        mainCollapsingToolbarLayout.disableTitle()
        mainImageView.setGone()
    }

    private fun lockActionBarWithAnimation() {
        //This is what performs the visible animation.
        mainAppBarLayout.apply {
            setExpanded(false, true)
            isActivated = false

        }
        /*
         * Sadly, setExpanded has no listener -- in order to ensure
         * that the unlock animation works as expected, we need to reset
         * the size of the mainAppBarLayout - that's why we execute this animation, that
         * actually is not seen because it is delayed until the setExpanded(false, true)
         * animation is done.
         */
        ValueAnimator
                .ofInt(resources.getDimension(R.dimen.action_bar_height_expanded).toInt(), resources.getDimension(R.dimen.action_bar_height_normal).toInt())
                .apply { duration = 300 }
                .also { it.startDelay = 200 }
                .also {
                    it.addUpdateListener {
                        val newHeight = it.animatedValue as Int
                        val lp = mainAppBarLayout.layoutParams as CoordinatorLayout.LayoutParams
                        lp.height = newHeight
                        mainAppBarLayout.layoutParams = lp
                    }
                }
                .also {
                    it.addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationStart(animation: Animator?) {
                            mainCollapsingToolbarLayout.disableTitle()
                            mainImageView.setGone()
                        }
                    })
                }
                .run { start() }
    }

    private fun unlockActionBarWithAnimation(animationEndListener: () -> Unit) {
        mainAppBarLayout.apply {
            setExpanded(true, false)
            isActivated = true
        }

        ValueAnimator
                .ofInt(mainAppBarLayout.measuredHeight, resources.getDimension(R.dimen.action_bar_height_expanded).toInt())
                .apply { duration = 500 }
                .also {
                    it.addUpdateListener {
                        val newHeight = it.animatedValue as Int
                        val lp = mainAppBarLayout.layoutParams as CoordinatorLayout.LayoutParams
                        lp.height = newHeight
                        mainAppBarLayout.layoutParams = lp
                    }
                }
                .also {
                    it.addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            mainImageView.setVisible()
                            animationEndListener.invoke()
                        }
                    })
                }
                .run { start() }
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


    /**
     * Inner [SearchView.OnQueryTextListener] implementation to handle the user searchPage over the
     * SearchView. It waits to submit the query a given amount of time that is based on the size
     * of the text introduced by the user.
     *
     * Note that this custom implementation could be a lot simpler using Android RxBindings, but
     * I don't want to bring RxJava into the project for this single reason.
     */
    private inner class QuerySubmitter(private val callback: (String) -> Unit) : SearchView.OnQueryTextListener {

        private lateinit var queryToSubmit: String
        private var isTyping = false
        private val typingTimeout = 1000L // 1 second
        private val timeoutHandler = Handler(Looper.getMainLooper())
        private val timeoutTask = Runnable {
            isTyping = false
            callback(queryToSubmit)
        }

        override fun onQueryTextSubmit(query: String): Boolean {
            timeoutHandler.removeCallbacks(timeoutTask)
            callback(query)
            return true
        }

        override fun onQueryTextChange(newText: String): Boolean {
            timeoutHandler.removeCallbacks(timeoutTask)
            if (newText.length > 3) {
                queryToSubmit = newText
                timeoutHandler.postDelayed(timeoutTask, typingTimeout)
            }
            return true
        }
    }

}