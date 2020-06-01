package com.jpp.mp.main.movies

sealed class MovieListEvent {

    class NavigateToMovieDetails(val movieId: String,
                                 val movieImageUrl: String,
                                 val movieTitle: String) : MovieListEvent()

}