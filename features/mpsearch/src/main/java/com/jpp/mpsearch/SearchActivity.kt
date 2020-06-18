package com.jpp.mpsearch

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.jpp.mp.common.viewmodel.MPGenericSavedStateViewModelFactory
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

class SearchActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var searchViewModelFactory: SearchViewModelFactory

    private val viewModel: SearchViewModel by viewModels {
        MPGenericSavedStateViewModelFactory(
            searchViewModelFactory,
            this
        )
    }

    private var searchToolBar: Toolbar? = null
    private var searchView: SearchView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_activity)

        setupViews()

        setSupportActionBar(searchToolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        viewModel.onInit()
    }

    override fun onDestroy() {
        searchToolBar = null
        searchView = null
        super.onDestroy()
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> =
        fragmentDispatchingAndroidInjector

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setupViews() {
        searchToolBar = findViewById(R.id.searchToolBar)
        searchView = findViewById(R.id.searchView)

        searchView?.isIconified = false
        searchView?.setIconifiedByDefault(false)
        searchView?.setOnQueryTextListener(QuerySubmitter { })
        searchView?.findViewById<View>(androidx.appcompat.R.id.search_close_btn)
            ?.setOnClickListener { }
    }
}