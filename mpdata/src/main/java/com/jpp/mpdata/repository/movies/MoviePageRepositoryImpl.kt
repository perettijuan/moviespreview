package com.jpp.mpdata.repository.movies

import com.jpp.mpdata.datasources.moviepage.MoviePageApi
import com.jpp.mpdata.datasources.moviepage.MoviePageDb
import com.jpp.mpdomain.MoviePage
import com.jpp.mpdomain.MovieSection
import com.jpp.mpdomain.Session
import com.jpp.mpdomain.SupportedLanguage
import com.jpp.mpdomain.UserAccount
import com.jpp.mpdomain.repository.MoviePageRepository

class MoviePageRepositoryImpl(
    private val moviePageApi: MoviePageApi,
    private val moviePageDb: MoviePageDb
) : MoviePageRepository {

    override suspend fun getMoviePageForSection(
        page: Int,
        section: MovieSection,
        language: SupportedLanguage
    ): MoviePage? {
        return moviePageDb.getMoviePageForSection(page, section)
            ?: getFromApi(page, section, language)
                ?.also { moviePage -> moviePageDb.saveMoviePageForSection(moviePage, section) }
    }

    override suspend fun flushMoviePagesForSection(section: MovieSection) {
        moviePageDb.flushAllPagesInSection(section)
    }

    override suspend fun getFavoriteMoviePage(
        page: Int,
        userAccount: UserAccount,
        session: Session,
        language: SupportedLanguage
    ): MoviePage? {
        return moviePageDb.getFavoriteMovies(page)
            ?: moviePageApi.getFavoriteMoviePage(page, userAccount, session, language)
                ?.also { moviePage -> moviePageDb.saveFavoriteMoviesPage(page, moviePage) }
    }

    override suspend fun getRatedMoviePage(
        page: Int,
        userAccount: UserAccount,
        session: Session,
        language: SupportedLanguage
    ): MoviePage? {
        return moviePageDb.getRatedMovies(page)
            ?: moviePageApi.getRatedMoviePage(page, userAccount, session, language)
                ?.also { moviePage -> moviePageDb.saveRatedMoviesPage(page, moviePage) }
    }

    override suspend fun getWatchlistMoviePage(
        page: Int,
        userAccount: UserAccount,
        session: Session,
        language: SupportedLanguage
    ): MoviePage? {
        return moviePageDb.getWatchlistMoviePage(page)
            ?: moviePageApi.getWatchlistMoviePage(page, userAccount, session, language)
                ?.also { moviePage -> moviePageDb.saveWatchlistMoviePage(page, moviePage) }
    }

    override suspend fun flushFavoriteMoviePages() {
        moviePageDb.flushFavoriteMoviePages()
    }

    override suspend fun flushRatedMoviePages() {
        moviePageDb.flushRatedMoviePages()
    }

    override suspend fun flushWatchlistMoviePages() {
        moviePageDb.flushWatchlistMoviePages()
    }

    private fun getFromApi(
        page: Int,
        section: MovieSection,
        language: SupportedLanguage
    ): MoviePage? = with(moviePageApi) {
        when (section) {
            MovieSection.Playing -> getNowPlayingMoviePage(page, language)
            MovieSection.TopRated -> getTopRatedMoviePage(page, language)
            MovieSection.Popular -> getPopularMoviePage(page, language)
            MovieSection.Upcoming -> getUpcomingMoviePage(page, language)
        }
    }
}
