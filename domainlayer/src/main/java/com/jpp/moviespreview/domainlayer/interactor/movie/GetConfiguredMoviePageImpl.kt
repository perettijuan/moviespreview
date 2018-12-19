package com.jpp.moviespreview.domainlayer.interactor.movie

import com.jpp.moviespreview.domainlayer.interactor.*

class GetConfiguredMoviePageImpl(private val getMoviePage: GetMoviePageInteractor,
                                 private val configureMovie: ConfigureMovieImagesInteractor) : GetConfiguredMoviePage {

    override fun execute(parameter: ConfiguredMoviePageParam?): ConfiguredMoviePageResult {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}