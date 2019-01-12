package com.jpp.moviespreview.screens.main.details

sealed class MovieDetailsFragmentViewState {
    object Loading : MovieDetailsFragmentViewState()
    object ErrorUnknown : MovieDetailsFragmentViewState()
    object ErrorNoConnectivity : MovieDetailsFragmentViewState()
    data class ShowDetail(val detail: MovieDetailsItem) : MovieDetailsFragmentViewState()
}