package com.jpp.mp.main.movies.fragments

import com.jpp.mp.R
import com.jpp.mp.main.movies.MovieListFragment
import com.jpp.mpdomain.MovieSection

class TopRatedMoviesFragment : MovieListFragment() {
    override val movieSection: MovieSection = MovieSection.TopRated
    override val screenTitle: String by lazy {
        resources.getString(R.string.main_menu_top_rated)
    }
}
