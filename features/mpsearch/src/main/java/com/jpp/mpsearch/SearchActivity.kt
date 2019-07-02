package com.jpp.mpsearch

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        setSupportActionBar(searchToolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
        }

        searchToolbar.contentInsetStartWithNavigation = 0

        with(mpSearchView) {
            isFocusable = true
            isIconified = false
            requestFocusFromTouch()
//            setOnQueryTextListener(QuerySubmitter { withSearchViewViewModel { search(it) } }
//            findViewById<View>(androidx.appcompat.R.id.search_close_btn).setOnClickListener {
//                withSearchViewViewModel { clearSearch() }
//            }
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.activity_enter_slide_right, R.anim.activity_exit_slide_left)
    }

    /**
     * Override in order to support exit navigation. Check [finish] to get the details of it.
     */
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}