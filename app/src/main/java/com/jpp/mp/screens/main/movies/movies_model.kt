package com.jpp.mp.screens.main.movies

import android.view.View
import androidx.paging.PagedList
import com.jpp.mpdesign.views.MPErrorView.ErrorViewState
import com.jpp.mpdomain.Movie as DomainMovie

/**
 * Represents the view state of the movies view (MoviesFragment).
 * Each subclass of this sealed class represents a particular state that the fragment
 * can assume.
 */
sealed class MoviesViewState {
    object Loading : MoviesViewState()
    object Refreshing : MoviesViewState()
    object ErrorNoConnectivity : MoviesViewState()
    object ErrorNoConnectivityWithItems : MoviesViewState()
    object ErrorUnknown : MoviesViewState()
    object ErrorUnknownWithItems : MoviesViewState()
    data class InitialPageLoaded(val pagedList: PagedList<MovieItem>) : MoviesViewState()
}

/**
 * Represents the view state of the movies list screen. This indicates that the view
 * can only render the view states modeled in this class.
 */
data class MovieListViewState(
        val loadingVisibility: Int = View.INVISIBLE,
        val errorViewState: ErrorViewState = ErrorViewState.asNotVisible(),
        val contentViewState: MovieListContentViewState = MovieListContentViewState()
) {
    companion object {
        fun showLoading() = MovieListViewState(loadingVisibility = View.VISIBLE)
        fun showUnknownError(errorHandler: () -> Unit) = MovieListViewState(errorViewState = ErrorViewState.asUnknownError(errorHandler))
        fun showNoConnectivityError(errorHandler: () -> Unit) = MovieListViewState(errorViewState = ErrorViewState.asConnectivity(errorHandler))
        fun showMovieList(pagedList: PagedList<MovieItem>) = MovieListViewState(contentViewState = MovieListContentViewState(visibility = View.VISIBLE, movieList = pagedList))
    }
}

/**
 * Represents the view state of the content shown in the movie list view.
 */
data class MovieListContentViewState(
        val visibility: Int = View.INVISIBLE,
        val movieList: PagedList<MovieItem>? = null
)


/**
 * Represents the navigation events that can be routed through the onSearch section.
 */
sealed class MoviesViewNavigationEvent {
    data class ToMovieDetails(val movieId: String, val movieImageUrl: String, val movieTitle: String, var positionInList: Int) : MoviesViewNavigationEvent()
}

/**
 * Represents an item in the list of Movies shown in the initial screen of the application.
 */
data class MovieItem(
        val movieId: Double,
        val headerImageUrl: String,
        val title: String,
        val contentImageUrl: String,
        val popularity: String,
        val voteCount: String
)