package com.jpp.mp.main.discover

import android.view.View
import com.jpp.mp.R
import com.jpp.mpdesign.views.MPErrorView

data class DiscoverMoviesViewState(
    val loadingVisibility: Int = View.INVISIBLE,
    val screenTitle: Int = R.string.main_menu_discover,
    val errorViewState: MPErrorView.ErrorViewState = MPErrorView.ErrorViewState.asNotVisible(),
    val contentViewState: DiscoverMoviesContentViewState = DiscoverMoviesContentViewState()
) {

    fun showUnknownError(errorHandler: () -> Unit): DiscoverMoviesViewState {
        return copy(
            loadingVisibility = View.INVISIBLE,
            errorViewState = MPErrorView.ErrorViewState.asUnknownError(errorHandler),
            contentViewState = DiscoverMoviesContentViewState()
        )
    }

    fun showNoConnectivityError(errorHandler: () -> Unit): DiscoverMoviesViewState {
        return copy(
            loadingVisibility = View.INVISIBLE,
            errorViewState = MPErrorView.ErrorViewState.asConnectivity(errorHandler),
            contentViewState = DiscoverMoviesContentViewState()
        )
    }

    fun showMovieList(movieList: List<DiscoveredMovieListItem>): DiscoverMoviesViewState {
        return copy(
            loadingVisibility = View.INVISIBLE,
            contentViewState = DiscoverMoviesContentViewState(
                visibility = View.VISIBLE,
                itemList = movieList
            ),
            errorViewState = MPErrorView.ErrorViewState.asNotVisible()
        )
    }

    companion object {
        fun showLoading(): DiscoverMoviesViewState = DiscoverMoviesViewState()
    }
}