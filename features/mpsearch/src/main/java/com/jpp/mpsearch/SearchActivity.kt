package com.jpp.mpsearch

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.jpp.mp.common.extensions.getScreenSizeInPixels
import dagger.android.AndroidInjection
import javax.inject.Inject

/**
 * [AppCompatActivity] to provide search functionality. The user can search for specific movies,
 * series and actors.
 */
class SearchActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val searchViewModel: SearchViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_activity)

        // Important to avoid blinks in transitions.
        // Source -> https://stackoverflow.com/questions/28364106/blinking-screen-on-image-transition-between-activities
        window.enterTransition = null

        findViewById<Toolbar>(R.id.searchToolbar)?.apply {
            setSupportActionBar(this)
        }

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        findViewById<SearchView>(R.id.searchView)?.apply {
            queryHint = getString(R.string.search_hint)
            isIconified = false
            setIconifiedByDefault(false)
        }

        searchViewModel.onInit(getScreenSizeInPixels().x)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}