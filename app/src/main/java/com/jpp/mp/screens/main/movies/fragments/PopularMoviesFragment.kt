package com.jpp.mp.screens.main.movies.fragments

import com.jpp.mp.screens.main.movies.MovieListFragment
import com.jpp.mp.screens.main.movies.MovieListViewModel

class PopularMoviesFragment : MovieListFragment() {

    override fun initViewModel(posterSize: Int, backdropSize: Int, vm: MovieListViewModel) {
        vm.onInitWithPopularSection(posterSize, backdropSize)
    }
}