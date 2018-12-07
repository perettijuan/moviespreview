package com.jpp.moviespreview.domainlayer.interactor.movie

import com.jpp.moviespreview.datalayer.repository.MoviesRepository
import com.jpp.moviespreview.domainlayer.ConnectivityVerifier
import com.jpp.moviespreview.domainlayer.MovieSection
import com.jpp.moviespreview.domainlayer.interactor.GetMoviePageUseCase
import com.jpp.moviespreview.domainlayer.interactor.MoviePageParam
import com.jpp.moviespreview.domainlayer.interactor.MoviePageResult

class GetMoviePageUseCaseImpl(private val moviesRepository: MoviesRepository,
                              private val mapper: MovieDomainMapper,
                              private val connectivityVerifier: ConnectivityVerifier) : GetMoviePageUseCase {


    override fun execute(parameter: MoviePageParam?): MoviePageResult {
        return parameter?.let {
            when (it.section) {
                MovieSection.Playing -> moviesRepository.getNowPlayingMoviePage(it.page)
                MovieSection.Popular -> moviesRepository.getPopularMoviePage(it.page)
                MovieSection.TopRated -> moviesRepository.getTopRatedMoviePage(it.page)
                MovieSection.Upcoming -> moviesRepository.getUpcomingMoviePage(it.page)
            }?.let { page ->
                MoviePageResult.Success(mapper.mapDataPageToDomainPage(page))
            } ?: run {
                when (connectivityVerifier.isConnectedToNetwork()) {
                    true -> MoviePageResult.ErrorUnknown
                    else -> MoviePageResult.ErrorNoConnectivity
                }
            }
        } ?: MoviePageResult.BadParams("MoviePageParam can not be null at this point")
    }
}