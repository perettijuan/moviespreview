package com.jpp.mpdata.repository.movies

import com.jpp.mpdata.datasources.moviepage.MoviesApi
import com.jpp.mpdata.datasources.moviepage.MoviesDb
import com.jpp.mpdomain.MoviePage
import com.jpp.mpdomain.MovieSection
import com.jpp.mpdomain.Session
import com.jpp.mpdomain.SupportedLanguage
import com.jpp.mpdomain.UserAccount
import com.jpp.mpdomain.repository.MoviePageRepository

class MoviePageRepositoryImpl(
    private val moviesApi: MoviesApi,
    private val moviesDb: MoviesDb
) : MoviePageRepository {

    override suspend fun getMoviePageForSection(page: Int, section: MovieSection, language: SupportedLanguage): MoviePage? {
        return moviesDb.getMoviePageForSection(page, section)
                ?: getFromApi(page, section, language)?.also { moviesDb.saveMoviePageForSection(it, section) }
    }

    override suspend fun flushMoviePagesForSection(section: MovieSection) {
        moviesDb.flushAllPagesInSection(section)
    }

    override suspend fun getFavoriteMoviePage(page: Int, userAccount: UserAccount, session: Session, language: SupportedLanguage): MoviePage? {
        return moviesDb.getFavoriteMovies(page)
                ?: moviesApi.getFavoriteMoviePage(page, userAccount, session, language)?.also { moviesDb.saveFavoriteMoviesPage(page, it) }
    }

    override suspend fun getRatedMoviePage(page: Int, userAccount: UserAccount, session: Session, language: SupportedLanguage): MoviePage? {
        return moviesDb.getRatedMovies(page)
                ?: moviesApi.getRatedMoviePage(page, userAccount, session, language)?.also { moviesDb.saveRatedMoviesPage(page, it) }
    }

    override suspend fun getWatchlistMoviePage(page: Int, userAccount: UserAccount, session: Session, language: SupportedLanguage): MoviePage? {
        return moviesDb.getWatchlistMoviePage(page)
                ?: moviesApi.getWatchlistMoviePage(page, userAccount, session, language)?.also { moviesDb.saveWatchlistMoviePage(page, it) }
    }

    override suspend fun flushFavoriteMoviePages() {
        moviesDb.flushFavoriteMoviePages()
    }

    override suspend fun flushRatedMoviePages() {
        moviesDb.flushRatedMoviePages()
    }

    override suspend fun flushWatchlistMoviePages() {
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
