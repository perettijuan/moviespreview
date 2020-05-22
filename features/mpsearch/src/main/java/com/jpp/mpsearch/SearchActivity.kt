package com.jpp.mpsearch

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jpp.mp.common.extensions.getScreenSizeInPixels
import com.jpp.mpsearch.databinding.SearchActivityBinding
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

    private var searchView: SearchView? = null
    private var searchResultRv: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<SearchActivityBinding>(this, R.layout.search_activity)

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

        searchView = findViewById<SearchView>(R.id.searchView)?.apply {
            queryHint = getString(R.string.search_hint)
            isIconified = false
            setIconifiedByDefault(false)
            setOnQueryTextListener(QuerySubmitter { searchViewModel.onSearch(it) })
            findViewById<View>(androidx.appcompat.R.id.search_close_btn)
                    .setOnClickListener { searchViewModel.onClearSearch() }
        }

        searchResultRv = findViewById<RecyclerView>(R.id.searchResultRv)?.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = SearchItemAdapter { item ->
                searchViewModel.onItemSelected(item)
            }
        }

        searchViewModel.viewState.observe(this, Observer { viewState ->
            binding.viewState = viewState

            (searchResultRv?.adapter as SearchItemAdapter)
                    .submitList(viewState.contentViewState.searchResultList)

            searchView?.apply {
                setQuery(viewState.searchQuery, false)
                clearFocus()
            }
        })

        searchViewModel.onInit(getScreenSizeInPixels().x)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onDestroy() {
        searchResultRv = null
        super.onDestroy()
    }
}