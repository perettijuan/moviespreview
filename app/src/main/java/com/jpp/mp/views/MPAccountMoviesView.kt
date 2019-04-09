package com.jpp.mp.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jpp.mp.R
import com.jpp.mp.ext.inflate
import com.jpp.mp.ext.loadImageUrl
import com.jpp.mp.ext.setInvisible
import com.jpp.mp.ext.setVisible
import kotlinx.android.synthetic.main.layout_account_movies.view.*
import kotlinx.android.synthetic.main.list_item_account_movies.view.*

/**
 * Custom [ConstraintLayout] to show the list of movies (either favorites or in watchlist) that the
 * user has in the account.
 */
class MPAccountMoviesView : ConstraintLayout {

    /**
     * Generic interface definition to load the list of items.
     */
    interface AccountMovieItem {
        fun getImageUrl(): String
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.MPAccountMoviesView)
        try {
            setTitle(typedArray.getText(R.styleable.MPAccountMoviesView_accountMoviesTitleText))
        } finally {
            typedArray.recycle()
        }

    }

    private fun init() {
        inflate(context, R.layout.layout_account_movies, this)
    }

    fun setTitle(title: CharSequence) {
        accountMoviesTitle.text = title
    }

    fun showLoading() {
        accountMoviesList.setInvisible()
        accountMoviesError.setInvisible()
        accountMoviesErrorActionButton.setInvisible()
        accountMoviesMoreIv.setInvisible()

        accountMoviesLoadingView.setVisible()
    }

    fun showMovies(movies: List<AccountMovieItem>, moreAction: () -> Unit) {
        accountMoviesLoadingView.setInvisible()
        accountMoviesError.setInvisible()
        accountMoviesErrorActionButton.setInvisible()

        accountMoviesMoreIv.apply {
            setVisible()
            setOnClickListener { moreAction.invoke() }
        }

        accountMoviesList.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            adapter = AccountMovieAdapter(movies)
            setVisible()
        }
    }

    fun showNoContent(@StringRes noContentText: Int) {
        accountMoviesList.setInvisible()
        accountMoviesLoadingView.setInvisible()
        accountMoviesMoreIv.setInvisible()
        accountMoviesErrorActionButton.setInvisible()

        accountMoviesError.setVisible()
        accountMoviesError.setText(noContentText)
    }

    fun showError(@StringRes errorStringRes: Int, retryAction: () -> Unit) {
        accountMoviesList.setInvisible()
        accountMoviesLoadingView.setInvisible()
        accountMoviesMoreIv.setInvisible()

        accountMoviesError.setVisible()
        accountMoviesError.setText(errorStringRes)
        accountMoviesErrorActionButton.setVisible()
        accountMoviesErrorActionButton.setOnClickListener { retryAction.invoke() }
    }

    class AccountMovieAdapter(private val items: List<AccountMovieItem>) : RecyclerView.Adapter<AccountMovieAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent.inflate(R.layout.list_item_account_movies))

        override fun getItemCount(): Int = items.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(items[position])
        }

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            fun bind(item: AccountMovieItem) {
                itemView.accountMoviesItemIv.loadImageUrl(item.getImageUrl())
            }
        }

    }

}