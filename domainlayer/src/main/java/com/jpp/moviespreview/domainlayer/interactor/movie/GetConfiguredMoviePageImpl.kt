package com.jpp.moviespreview.domainlayer.interactor.movie

import com.jpp.moviespreview.domainlayer.MoviePage
import com.jpp.moviespreview.domainlayer.interactor.*

/**
 * THIS IS A VERY SPECIAL CASE WHERE WE COMBINE FUNCTIONALITY ENCAPSULATED IN SMALLER INTERACTORS IN
 * AN INTERACTOR OF GENERAL PURPOSE IN ORDER TO SIMPLIFY THE CLIENT CODE.
 */
class GetConfiguredMoviePageImpl(private val getMoviePage: GetMoviePage,
                                 private val configureMovie: ConfigureMovieImages) : GetConfiguredMoviePage {

    override fun execute(parameter: ConfiguredMoviePageParam): ConfiguredMoviePageResult {
        return getMoviePage(MoviePageParam(parameter.page, parameter.section)).let {
            when (it) {
                MoviePageResult.ErrorNoConnectivity -> ConfiguredMoviePageResult.ErrorNoConnectivity
                MoviePageResult.ErrorUnknown -> ConfiguredMoviePageResult.ErrorUnknown
                is MoviePageResult.Success -> {
                    it.moviePage.movies
                            .map { movieToConfigure -> configureMovie(MovieImagesParam(movieToConfigure, parameter.backdropSize, parameter.posterSize)).movie }
                            .let { configuredMovies ->
                                ConfiguredMoviePageResult.Success(
                                        MoviePage(
                                                pageNumber = it.moviePage.pageNumber,
                                                totalPages = it.moviePage.totalPages,
                                                movies = configuredMovies
                                        )
                                )
                            }
                }
            }
        }
    }
}