package com.jpp.moviespreview.domainlayer.interactor.movie

import com.jpp.moviespreview.domainlayer.ConnectivityVerifier
import com.jpp.moviespreview.domainlayer.MovieSection
import com.jpp.moviespreview.domainlayer.interactor.GetMoviePageInteractor
import com.jpp.moviespreview.domainlayer.interactor.MoviePageParam
import com.jpp.moviespreview.domainlayer.interactor.MoviePageResult
import com.jpp.moviespreview.domainlayer.repository.MoviesRepository

class GetMoviePageInteractorImpl(private val moviesRepository: MoviesRepository,
                                 private val connectivityVerifier: ConnectivityVerifier) : GetMoviePageInteractor {


    override fun execute(parameter: MoviePageParam?): MoviePageResult {
        return parameter?.let {
            when (it.section) {
                MovieSection.Playing -> moviesRepository.getNowPlayingMoviePage(it.page)
                MovieSection.Popular -> moviesRepository.getPopularMoviePage(it.page)
                MovieSection.TopRated -> moviesRepository.getTopRatedMoviePage(it.page)
                MovieSection.Upcoming -> moviesRepository.getUpcomingMoviePage(it.page)
            }.let { result ->
                when (result) {
                    is MoviesRepository.MoviesRepositoryOutput.Success -> MoviePageResult.Success(result.page)
                    else -> {
                        when (connectivityVerifier.isConnectedToNetwork()) {
                            true -> MoviePageResult.ErrorUnknown
                            else -> MoviePageResult.ErrorNoConnectivity
                        }
                    }
                }
            }
        } ?: MoviePageResult.BadParams("MoviePageParam can not be null at this point")
    }
}