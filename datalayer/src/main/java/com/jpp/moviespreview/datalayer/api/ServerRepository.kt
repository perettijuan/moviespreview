package com.jpp.moviespreview.datalayer.api

import com.jpp.moviespreview.datalayer.BuildConfig
import com.jpp.moviespreview.datalayer.repository.configuration.ConfigurationServer
import com.jpp.moviespreview.domainlayer.AppConfiguration
import com.jpp.moviespreview.domainlayer.MovieDetail
import com.jpp.moviespreview.domainlayer.MoviePage
import com.jpp.moviespreview.domainlayer.repository.MoviesRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


/**
 * Represents the server access (the movieDB API) for the repository layer.
 * It implements all the repository definition in the repository contract in order
 * to provide a single point of access to the API.
 * It hides the Retrofit implementation to the clients of the data layer.
 */
class ServerRepository(private val serverApiKey: String)
    : ConfigurationServer,
        MoviesRepository {

    companion object {
        val API: TheMovieDBApi by lazy {
            Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(BuildConfig.API_ENDPOINT)
                    .client(OkHttpClient
                            .Builder()
                            .addInterceptor(HttpLoggingInterceptor().apply {
                                level = HttpLoggingInterceptor.Level.BODY
                            }).build())
                    .build()
                    .create(TheMovieDBApi::class.java)
        }
    }

    override fun getAppConfiguration(): AppConfiguration? = tryCatchOrReturnNull { API.getAppConfiguration(serverApiKey).execute().body() }

    override fun getNowPlayingMoviePage(page: Int): MoviesRepository.MoviesRepositoryOutput =
        getMoviePage { API.getNowPlaying(page, serverApiKey).execute().body() }

    override fun getPopularMoviePage(page: Int): MoviesRepository.MoviesRepositoryOutput =
        getMoviePage { API.getPopular(page, serverApiKey).execute().body() }

    override fun getTopRatedMoviePage(page: Int): MoviesRepository.MoviesRepositoryOutput =
        getMoviePage { API.getTopRated(page, serverApiKey).execute().body() }

    override fun getUpcomingMoviePage(page: Int): MoviesRepository.MoviesRepositoryOutput =
        getMoviePage { API.getUpcoming(page, serverApiKey).execute().body() }

    override fun getMovieDetail(movieId: Double): MoviesRepository.MoviesRepositoryOutput =
        tryCatchOrReturnNull { API.getMovieDetails(movieId, serverApiKey).execute().body() }
                ?.let { MoviesRepository.MoviesRepositoryOutput.MovieDetailsRetrieved(it) }
                ?: let { MoviesRepository.MoviesRepositoryOutput.Error }

    override fun updateNowPlayingMoviePage(moviePage: MoviePage) =
        throw UnsupportedOperationException("Updating playing movies is not supported by the server")

    override fun updatePopularMoviePage(moviePage: MoviePage) =
        throw UnsupportedOperationException("Updating popular movies is not supported by the server")

    override fun updateTopRatedMoviePage(moviePage: MoviePage) =
        throw UnsupportedOperationException("Updating top rated movies is not supported by the server")

    override fun updateUpcomingMoviePage(moviePage: MoviePage) =
        throw UnsupportedOperationException("Updating upcoming movies is not supported by the server")

    override fun updateMovieDetail(movieDetail: MovieDetail) =
        throw UnsupportedOperationException("Updating a movie detail is not supported by the server")

    /**
     * Support method to encapsulate the movie retrieval logic. It receives a function as parameter that
     * takes care of executing the API call.
     */
    private fun getMoviePage(apiCall: () -> MoviePage?): MoviesRepository.MoviesRepositoryOutput =
        tryCatchOrReturnNull { apiCall.invoke() }
                ?.let { MoviesRepository.MoviesRepositoryOutput.MoviePageRetrieved(it) }
                ?: let { MoviesRepository.MoviesRepositoryOutput.Error }


    /**
     * Executes the provided [block] in a try-catch block and returns the result.
     * If the [block] fails with an exception, null is returned.
     */
    private inline fun <T : Any> tryCatchOrReturnNull(block: () -> T?): T? {
        return try {
            block()
        } catch (ex: Exception) {
            null
        }
    }
}