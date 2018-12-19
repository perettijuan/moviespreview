package com.jpp.moviespreview.domainlayer.interactor.movie

import com.jpp.moviespreview.domainlayer.ConnectivityVerifier
import com.jpp.moviespreview.domainlayer.MovieSection
import com.jpp.moviespreview.domainlayer.interactor.GetMoviePage
import com.jpp.moviespreview.domainlayer.interactor.MoviePageParam
import com.jpp.moviespreview.domainlayer.interactor.MoviePageResult
import com.jpp.moviespreview.domainlayer.repository.MoviesRepository

class GetMoviePageImpl(private val moviesRepository: MoviesRepository,
                       private val connectivityVerifier: ConnectivityVerifier) : GetMoviePage {


    override fun execute(parameter: MoviePageParam): MoviePageResult {
        return when (parameter.section) {
            MovieSection.Playing -> moviesRepository.getNowPlayingMoviePage(parameter.page)
            MovieSection.Popular -> moviesRepository.getPopularMoviePage(parameter.page)
            MovieSection.TopRated -> moviesRepository.getTopRatedMoviePage(parameter.page)
            MovieSection.Upcoming -> moviesRepository.getUpcomingMoviePage(parameter.page)
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
    }
}