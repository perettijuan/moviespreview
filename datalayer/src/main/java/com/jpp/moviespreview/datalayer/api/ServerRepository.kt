package com.jpp.moviespreview.datalayer.api

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

    override fun getConfiguration(): ImagesConfiguration? =
        tryCatchOrReturnNull { API.getAppConfiguration(serverApiKey).execute().body() }?.let { mapper.mapDataAppConfiguration(it) }

    override fun updateAppConfiguration(imagesConfiguration: ImagesConfiguration) =
        throw UnsupportedOperationException("Updating AppConfiguration is not supported by the server")

    override fun getNowPlayingMoviePage(page: Int): MoviePage? =
        tryCatchOrReturnNull { API.getNowPlaying(page, serverApiKey).execute().body() }?.let { mapper.mapDataMoviePage(it) }

    override fun getPopularMoviePage(page: Int): MoviePage? =
        tryCatchOrReturnNull { API.getPopular(page, serverApiKey).execute().body() }?.let { mapper.mapDataMoviePage(it) }

    override fun getTopRatedMoviePage(page: Int): MoviePage? =
        tryCatchOrReturnNull { API.getTopRated(page, serverApiKey).execute().body() }?.let { mapper.mapDataMoviePage(it) }

    override fun getUpcomingMoviePage(page: Int): MoviePage? =
        tryCatchOrReturnNull { API.getUpcoming(page, serverApiKey).execute().body() }?.let { mapper.mapDataMoviePage(it) }

    override fun updateNowPlayingMoviePage(moviePage: MoviePage) =
        throw UnsupportedOperationException("Updating playing movies is not supported by the server")

    override fun updatePopularMoviePage(moviePage: MoviePage) =
        throw UnsupportedOperationException("Updating popular movies is not supported by the server")

    override fun updateTopRatedMoviePage(moviePage: MoviePage) =
        throw UnsupportedOperationException("Updating top rated movies is not supported by the server")

    override fun updateUpcomingMoviePage(moviePage: MoviePage) =
        throw UnsupportedOperationException("Updating upcoming movies is not supported by the server")

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