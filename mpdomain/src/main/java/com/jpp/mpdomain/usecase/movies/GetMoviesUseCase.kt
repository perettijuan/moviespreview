package com.jpp.mpdomain.usecase.movies

import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.MoviePage
import com.jpp.mpdomain.MovieSection
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.LanguageRepository
import com.jpp.mpdomain.repository.MoviePageRepository

/**
 * Defines a UseCase that fetches movies for a particular section.
 * UseCase definition: verify if there's internet connection (the application does not work if
 * not connected). If connected, fetch the movies for the provided section.
 * If not connected, return an error that indicates such state.
 */
interface GetMoviesUseCase {

    /**
     * Represents the result of a a movies fetching execution.
     */
    sealed class GetMoviesResult {
        object ErrorNoConnectivity : GetMoviesResult()
        object ErrorUnknown : GetMoviesResult()
        data class Success(val moviesPage: MoviePage) : GetMoviesResult()
    }

    /**
     * Performs the fetch of the movies page identified by [page] for the provided [section].
     * @return
     *  - [GetMoviesResult.Success] when there is internet connectivity and the movie page is fetched.
     *  - [GetMoviesResult.ErrorNoConnectivity] when the UC detects that the application has no internet connectivity.
     *  - [GetMoviesResult.ErrorUnknown] when an error occur while fetching the page.
     */
    fun getMoviePageForSection(page: Int, section: MovieSection): GetMoviesResult


    class Impl(private val moviePageRepository: MoviePageRepository,
               private val connectivityRepository: ConnectivityRepository,
               private val languageRepository: LanguageRepository) : GetMoviesUseCase {

        override fun getMoviePageForSection(page: Int, section: MovieSection): GetMoviesResult {
            return when (connectivityRepository.getCurrentConnectivity()) {
                Connectivity.Disconnected -> GetMoviesResult.ErrorNoConnectivity
                Connectivity.Connected -> moviePageRepository.getMoviePageForSection(page, section, languageRepository.getCurrentAppLanguage())?.let {
                    GetMoviesResult.Success(it)
                } ?: run {
                    GetMoviesResult.ErrorUnknown
                }
            }
        }
    }
}