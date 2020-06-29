package com.jpp.mpaccount.account

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jpp.mpaccount.R
import com.jpp.mpdesign.ext.inflate
import com.jpp.mpdesign.ext.setInvisible
import com.jpp.mpdesign.ext.setVisible
import com.jpp.mpdesign.views.MPImageView

/**
 * Custom [ConstraintLayout] used in the account section to show the list
 * of user's movies (Favorite, Rated and/or in WatchList) in a horizontal
 * list.
 */
class UserAccountMoviesView : ConstraintLayout {

    /**
     * Generic interface definition to load the list of items.
     */
    interface UserAccountMovieItem {
        fun getImageUrl(): String
    }

    private var userAccountMoviesTitle: TextView? = null
    private var userAccountMoviesList: RecyclerView? = null
    private var userAccountMoviesError: TextView? = null
    private var userAccountMoviesMoreIv: ImageView? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.UserAccountMoviesView)
        try {
            setTitle(typedArray.getText(R.styleable.UserAccountMoviesView_accountMoviesTitleText))
        } finally {
            typedArray.recycle()
        }
    }

    private fun init() {
        inflate(context, R.layout.layout_user_account_movies, this)
        userAccountMoviesTitle = findViewById(R.id.userAccountMoviesTitle)
        userAccountMoviesList = findViewById(R.id.userAccountMoviesList)
        userAccountMoviesError = findViewById(R.id.userAccountMoviesError)
        userAccountMoviesMoreIv = findViewById(R.id.userAccountMoviesMoreIv)
    }

    fun setTitle(title: CharSequence) {
        userAccountMoviesTitle?.text = title
    }

    fun items(movies: List<UserAccountMovieItem>?) {
        if (movies == null) {
            return
        }

        userAccountMoviesList?.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            adapter = AccountMovieAdapter(movies)
            setVisible()
        }

        userAccountMoviesError?.setInvisible()
    }

    fun errorMessage(message: Int) {
        if (message == 0) {
            return
        }

        userAccountMoviesMoreIv?.setInvisible()
        userAccountMoviesList?.setInvisible()

        userAccountMoviesError?.apply {
            setText(message)
            setVisible()
        }
    }

    class AccountMovieAdapter(private val items: List<UserAccountMovieItem>) :
        RecyclerView.Adapter<AccountMovieAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(parent.inflate(R.layout.list_item_user_account_movies))

        override fun getItemCount(): Int = items.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(items[position])
        }

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            fun bind(item: UserAccountMovieItem) {
                val userAccountMoviesItemIv: MPImageView =
                    itemView.findViewById(R.id.userAccountMoviesItemIv)
                userAccountMoviesItemIv.imageUrl(item.getImageUrl())
            }
        }
    }
}
