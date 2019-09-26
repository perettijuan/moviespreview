package com.jpp.mp.main.movies.fragments

import com.jpp.mp.main.movies.MovieListFragment
import com.jpp.mp.main.movies.MovieListParam
import com.jpp.mp.main.movies.MovieListViewModel

class UpcomingMoviesFragment : MovieListFragment() {

    override fun initViewModel(posterSize: Int, backdropSize: Int, vm: MovieListViewModel) {
        vm.onInit(MovieListParam.upcoming(resources, posterSize, backdropSize))
    }
}