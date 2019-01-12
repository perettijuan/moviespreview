package com.jpp.moviespreview.screens.main.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jpp.moviespreview.screens.MPScopedViewModel
import com.jpp.mpdomain.repository.details.MovieDetailsRepository
import com.jpp.mpdomain.repository.details.MovieDetailsRepositoryState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MovieDetailsViewModel(private val detailsRepository: MovieDetailsRepository) : MPScopedViewModel() {

    private val viewState by lazy { MutableLiveData<MovieDetailsFragmentViewState>() }


    fun init(movieId: Double) {
        viewState.postValue(MovieDetailsFragmentViewState.Loading)
        launch {
            viewState.postValue(withContext(Dispatchers.Default) { fetchMovieDetail(movieId) })
        }
    }

    fun viewState(): LiveData<MovieDetailsFragmentViewState> = viewState

    private fun fetchMovieDetail(movieId: Double): MovieDetailsFragmentViewState =
        detailsRepository.getDetail(movieId)
                .let {
                    when (it) {
                        MovieDetailsRepositoryState.ErrorUnknown -> MovieDetailsFragmentViewState.ErrorUnknown
                        MovieDetailsRepositoryState.ErrorNoConnectivity -> MovieDetailsFragmentViewState.ErrorNoConnectivity
                        is MovieDetailsRepositoryState.Success -> {
                            with(it.detail) {
                                MovieDetailsFragmentViewState.ShowDetail(
                                        MovieDetailsItem(
                                                title = title,
                                                overview = overview,
                                                releaseDate = release_date,
                                                voteCount = vote_count,
                                                voteAverage = vote_average,
                                                popularity = popularity
                                        )
                                )
                            }
                        }
                    }
                }

}