package com.jpp.moviespreview.datalayer.api

import com.jpp.moviespreview.datalayer.MoviePage as DataMoviePage
import com.jpp.moviespreview.datalayer.BuildConfig
import com.jpp.moviespreview.datalayer.DataModelMapper
import com.jpp.moviespreview.domainlayer.ImagesConfiguration
import com.jpp.moviespreview.domainlayer.MoviePage
import com.jpp.moviespreview.domainlayer.repository.ConfigurationRepository
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
class ServerRepository(private val serverApiKey: String,
                       private val mapper: DataModelMapper)
    : ConfigurationRepository,
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

    override fun getConfiguration(): ConfigurationRepository.ConfigurationRepositoryOutput =
        tryCatchOrReturnNull { API.getAppConfiguration(serverApiKey).execute().body() }
                ?.let { ConfigurationRepository.ConfigurationRepositoryOutput.Success(mapper.mapDataAppConfiguration(it)) }
                ?: run { ConfigurationRepository.ConfigurationRepositoryOutput.Error }

    override fun updateAppConfiguration(imagesConfiguration: ImagesConfiguration) =
        throw UnsupportedOperationException("Updating AppConfiguration is not supported by the server")

    override fun getNowPlayingMoviePage(page: Int): MoviesRepository.MoviesRepositoryOutput =
        getMoviePage { API.getNowPlaying(page, serverApiKey).execute().body() }

    override fun getPopularMoviePage(page: Int): MoviesRepository.MoviesRepositoryOutput =
        getMoviePage { API.getPopular(page, serverApiKey).execute().body() }

    override fun getTopRatedMoviePage(page: Int): MoviesRepository.MoviesRepositoryOutput =
        getMoviePage { API.getTopRated(page, serverApiKey).execute().body() }

    override fun getUpcomingMoviePage(page: Int): MoviesRepository.MoviesRepositoryOutput =
        getMoviePage { API.getUpcoming(page, serverApiKey).execute().body() }

    override fun updateNowPlayingMoviePage(moviePage: MoviePage) =
        throw UnsupportedOperationException("Updating playing movies is not supported by the server")

    override fun updatePopularMoviePage(moviePage: MoviePage) =
        throw UnsupportedOperationException("Updating popular movies is not supported by the server")

    override fun updateTopRatedMoviePage(moviePage: MoviePage) =
        throw UnsupportedOperationException("Updating top rated movies is not supported by the server")

    override fun updateUpcomingMoviePage(moviePage: MoviePage) =
        throw UnsupportedOperationException("Updating upcoming movies is not supported by the server")


    /**
     * Support method to encapsulate the movie retrieval logic. It receives a function as parameter that
     * takes care of executing the API call.
     */
    private fun getMoviePage(apiCall: () -> DataMoviePage?): MoviesRepository.MoviesRepositoryOutput =
        tryCatchOrReturnNull { apiCall.invoke() }
                ?.let { MoviesRepository.MoviesRepositoryOutput.Success(mapper.mapDataMoviePage(it)) }
                ?: run { MoviesRepository.MoviesRepositoryOutput.Error }


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