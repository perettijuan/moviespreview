package com.jpp.mpsearch

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.jpp.mp.common.extensions.observeValue
import com.jpp.mp.common.viewmodel.MPGenericSavedStateViewModelFactory
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

/**
 * Activity to provide search functionality.
 *
 * Interacts with [SearchViewModel] to provide search functionality. Since the [SearchView] that
 * allows the user to enter a query is located in this Activity's [Toolbar], the ViewModel
 * updates the state of that [SearchView] using [SearchViewViewState]. The content of the search
 * and rendered using [SearchFragment].
 */
class SearchActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var searchViewModelFactory: SearchViewModelFactory

    @Inject
    lateinit var searchNavigator: SearchNavigator

    private val viewModel: SearchViewModel by viewModels {
        MPGenericSavedStateViewModelFactory(
            searchViewModelFactory,
            this
        )
    }

    private var searchToolBar: Toolbar? = null
    private var searchView: SearchView? = null

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_activity)

        setupViews()

        setSupportActionBar(searchToolBar)

        setupNavigation()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        viewModel.searchViewState.observeValue(this, ::renderViewState)
        viewModel.onInit()
    }

    override fun onSupportNavigateUp(): Boolean {
        val handled = NavigationUI.navigateUp(
            findNavController(R.id.searchNavHostFragment),
            appBarConfiguration
        )
        return if (handled) {
            handled
        } else {
            onBackPressed()
            super.onSupportNavigateUp()
        }
    }

    override fun onResume() {
        super.onResume()
        searchNavigator.bind(findNavController(R.id.searchNavHostFragment))
    }

    override fun onPause() {
        super.onPause()
        searchNavigator.unBind()
    }

    override fun onDestroy() {
        searchToolBar = null
        searchView = null
        super.onDestroy()
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> =
        fragmentDispatchingAndroidInjector

    private fun setupViews() {
        searchToolBar = findViewById(R.id.searchToolBar)
        searchView = findViewById(R.id.searchView)

        searchView?.isIconified = false
        searchView?.setIconifiedByDefault(false)
        searchView?.setOnQueryTextListener(QuerySubmitter { query -> viewModel.onSearch(query) })
        searchView?.findViewById<View>(androidx.appcompat.R.id.search_close_btn)
            ?.setOnClickListener { viewModel.onClearSearch() }
    }

    private fun renderViewState(viewState: SearchViewViewState) {
        searchView?.setQuery(viewState.searchQuery, false)
        searchView?.queryHint = getString(viewState.queryHint)
        searchView?.setFocus(viewState.focused)
    }

    private fun SearchView.setFocus(focus: Boolean) {
        if (focus) requestFocus() else clearFocus()
    }

    /**
     * Prepare the navigation components by setting the fragments that are going to be used
     * as top level destinations of the drawer and adding a navigation listener to update
     * the view state that the ViewModel is showing.
     */
    private fun setupNavigation() {
        findNavController(R.id.searchNavHostFragment).let { navController ->
            /*
             * We want several top-level destinations since we're showing the
             * navigation drawer.
             */
            appBarConfiguration = AppBarConfiguration(navController.graph)
            NavigationUI.setupActionBarWithNavController(
                this,
                navController,
                appBarConfiguration
            )
        }
    }
}