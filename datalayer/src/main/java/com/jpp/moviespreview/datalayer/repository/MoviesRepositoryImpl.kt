package com.jpp.moviespreview.datalayer.repository


import com.jpp.moviespreview.domainlayer.MoviePage
import com.jpp.moviespreview.domainlayer.repository.MoviesRepository

class MoviesRepositoryImpl(private val cacheRepository: MoviesRepository,
                           private val serverRepository: MoviesRepository) : MoviesRepository {

    override fun getNowPlayingMoviePage(page: Int): MoviePage? {
        return getMoviePage(page = page,
                dbFunction = { cacheRepository.getNowPlayingMoviePage(it) },
                serverFunction = { serverRepository.getNowPlayingMoviePage(it) },
                updateFunction = { cacheRepository.updateNowPlayingMoviePage(it) }
        )
    }

    override fun getPopularMoviePage(page: Int): MoviePage? {
        return getMoviePage(page = page,
                dbFunction = { cacheRepository.getPopularMoviePage(it) },
                serverFunction = { serverRepository.getPopularMoviePage(it) },
                updateFunction = { cacheRepository.updatePopularMoviePage(it) }
        )
    }

    override fun getTopRatedMoviePage(page: Int): MoviePage? {
        return getMoviePage(page = page,
                dbFunction = { cacheRepository.getTopRatedMoviePage(it) },
                serverFunction = { serverRepository.getTopRatedMoviePage(it) },
                updateFunction = { cacheRepository.updateTopRatedMoviePage(it) }
        )
    }

    override fun getUpcomingMoviePage(page: Int): MoviePage? {
        return getMoviePage(page = page,
                dbFunction = { cacheRepository.getUpcomingMoviePage(it) },
                serverFunction = { serverRepository.getUpcomingMoviePage(it) },
                updateFunction = { cacheRepository.updateUpcomingMoviePage(it) }
        )
    }


    override fun updateNowPlayingMoviePage(moviePage: MoviePage) {
        throw UnsupportedOperationException("Updating MoviePage is not supported by the application")
    }

    override fun updatePopularMoviePage(moviePage: MoviePage) {
        throw UnsupportedOperationException("Updating MoviePage is not supported by the application")
    }

    override fun updateTopRatedMoviePage(moviePage: MoviePage) {
        throw UnsupportedOperationException("Updating MoviePage is not supported by the application")
    }

    override fun updateUpcomingMoviePage(moviePage: MoviePage) {
        throw UnsupportedOperationException("Updating MoviePage is not supported by the application")
    }

    /**
     * Helper function to retrieve a [MoviePage].
     * Attempt to load the page from the local storage using [dbFunction].
     * If this fails, executes the [serverFunction] and updates the local storage
     * using [updateFunction] if the execution is successful.
     */
    private fun getMoviePage(page: Int,
                             dbFunction: (Int) -> MoviePage?,
                             serverFunction: (Int) -> MoviePage?,
                             updateFunction: (MoviePage) -> Unit): MoviePage? {
        return dbFunction.invoke(page) ?: serverFunction.invoke(page)?.apply(updateFunction)
    }
}