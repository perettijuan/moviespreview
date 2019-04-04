package com.jpp.mpdata.repository.movies

import com.jpp.mpdomain.*
import com.jpp.mpdomain.repository.MoviesRepository

class MoviesRepositoryImpl(private val moviesApi: MoviesApi,
                           private val moviesDb: MoviesDb) : MoviesRepository {

    override fun getMoviePageForSection(page: Int, section: MovieSection, language: SupportedLanguage): MoviePage? {
        return moviesDb.getMoviePageForSection(page, section) ?: run {
            getFromApi(page, section, language)?.also {
                moviesDb.saveMoviePageForSection(it, section)
            }
        }
    }

    override fun getMovieDetails(movieId: Double, language: SupportedLanguage): MovieDetail? {
        return moviesDb.getMovieDetails(movieId) ?: run {
            moviesApi.getMovieDetails(movieId, language)?.also {
                moviesDb.saveMovieDetails(it)
            }
        }
    }

    override fun getMovieAccountState(movieId: Double, session: Session): MovieAccountState? {
        /*
         * TODO JPP for the moment, we don't store this state in the local storage
         * BUT it is a great candidate to store it and try to use the WorkManager
         * to sync the state with the API
         */
        return moviesApi.getMovieAccountState(movieId, session)
    }

    override fun updateMovieFavoriteState(movieId: Double, asFavorite: Boolean, userAccount: UserAccount, session: Session): Boolean {
        return moviesApi.updateMovieFavoriteState(movieId, asFavorite, userAccount, session) ?: false
    }

    private fun getFromApi(page: Int, section: MovieSection, language: SupportedLanguage): MoviePage? = with(moviesApi) {
        when (section) {
            MovieSection.Playing -> getNowPlayingMoviePage(page, language)
            MovieSection.TopRated -> getTopRatedMoviePage(page, language)
            MovieSection.Popular -> getPopularMoviePage(page, language)
            MovieSection.Upcoming -> getUpcomingMoviePage(page, language)
        }
    }
}