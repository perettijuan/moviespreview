package com.jpp.mpdomain.usecase.movies

import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.MovieSection
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.MoviesRepository

/**
 * Defines a UseCase that fetches movies for a particular section.
 * UseCase definition: verify if there's internet connection (the application does not work if
 * not connected). If connected, fetch the movies for the provided section.
 * If not connected, return an error that indicates such state.
 */
interface GetMoviesUseCase {
    /**
     * Performs the fetch of the movies page identified by [page] for the provided [section].
     * @return
     *  - [GetMoviesUseCaseResult.Success] when there is internet connectivity and the movie page is fetched.
     *  - [GetMoviesUseCaseResult.ErrorNoConnectivity] when the UC detects that the application has no internet connectivity.
     *  - [GetMoviesUseCaseResult.ErrorUnknown] when an error occur while fetching the page.
     */
    fun getMoviePageForSection(page: Int, section: MovieSection): GetMoviesUseCaseResult


    class Impl(private val moviesRepository: MoviesRepository,
               private val connectivityRepository: ConnectivityRepository) : GetMoviesUseCase {

        override fun getMoviePageForSection(page: Int, section: MovieSection): GetMoviesUseCaseResult {
            return when (connectivityRepository.getCurrentConnectivity()) {
                Connectivity.Disconnected -> GetMoviesUseCaseResult.ErrorNoConnectivity
                Connectivity.Connected -> moviesRepository.getMoviePageForSection(page, section)?.let {
                    GetMoviesUseCaseResult.Success(it)
                } ?: run {
                    GetMoviesUseCaseResult.ErrorUnknown
                }
            }
        }
    }
}