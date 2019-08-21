package com.jpp.mpdata.repository.movies

import com.jpp.mpdata.datasources.moviepage.MoviesApi
import com.jpp.mpdata.datasources.moviepage.MoviesDb
import com.jpp.mpdomain.*
import com.jpp.mpdomain.repository.MoviePageRepository

class MoviePageRepositoryImpl(private val moviesApi: MoviesApi,
                              private val moviesDb: MoviesDb) : MoviePageRepository {

    override fun getMoviePageForSection(page: Int, section: MovieSection, language: SupportedLanguage): MoviePage? {
        return moviesDb.getMoviePageForSection(page, section)
                ?: getFromApi(page, section, language)?.also { moviesDb.saveMoviePageForSection(it, section) }
    }

    override fun flushMoviePagesForSection(section: MovieSection) {
        moviesDb.flushAllPagesInSection(section)
    }

    override fun getFavoriteMoviePage(page: Int, userAccount: UserAccount, session: Session, language: SupportedLanguage): MoviePage? {
        return moviesDb.getFavoriteMovies(page)
                ?: moviesApi.getFavoriteMoviePage(page, userAccount, session, language)?.also { moviesDb.saveFavoriteMoviesPage(page, it) }
    }

    override fun getRatedMoviePage(page: Int, userAccount: UserAccount, session: Session, language: SupportedLanguage): MoviePage? {
        return moviesDb.getRatedMovies(page)
                ?: moviesApi.getRatedMoviePage(page, userAccount, session, language)?.also { moviesDb.saveRatedMoviesPage(page, it) }
    }

    override fun getWatchlistMoviePage(page: Int, userAccount: UserAccount, session: Session, language: SupportedLanguage): MoviePage? {
        return moviesDb.getWatchlistMoviePage(page)
                ?: moviesApi.getWatchlistMoviePage(page, userAccount, session, language)?.also { moviesDb.saveWatchlistMoviePage(page, it) }
    }

    override fun flushFavoriteMoviePages() {
        moviesDb.flushFavoriteMoviePages()
    }

    override fun flushRatedMoviePages() {
        moviesDb.flushRatedMoviePages()
    }

    override fun flushWatchlistMoviePages() {
        moviesDb.flushWatchlistMoviePages()
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