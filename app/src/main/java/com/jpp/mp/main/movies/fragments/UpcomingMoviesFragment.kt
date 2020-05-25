package com.jpp.mp.main.movies.fragments

import com.jpp.mp.R
import com.jpp.mp.main.movies.MovieListFragment
import com.jpp.mpdomain.MovieSection

class UpcomingMoviesFragment : MovieListFragment() {
    override val movieSection: MovieSection = MovieSection.Upcoming
    override val screenTitle: String by lazy {
        resources.getString(R.string.main_menu_upcoming)
    }
}
