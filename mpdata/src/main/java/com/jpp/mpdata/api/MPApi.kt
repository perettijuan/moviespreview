package com.jpp.mpdata.api

import com.jpp.mpdata.BuildConfig
import com.jpp.mpdata.repository.movies.MoviesApi
import com.jpp.mpdata.repository.search.SearchApi
import com.jpp.mpdomain.AppConfiguration
import com.jpp.mpdomain.MovieDetail
import com.jpp.mpdomain.MoviePage
import com.jpp.mpdomain.SearchPage
import com.jpp.mpdata.repository.configuration.ConfigurationApi
import com.jpp.mpdomain.repository.details.MovieDetailsApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Represents the remote API that MoviesPreview supports. It is a wrapper
 * around Retrofit classes to provide a clean access to the API.
 */
open class MPApi
    : ConfigurationApi,
        MoviesApi,
        MovieDetailsApi,
        SearchApi {

    override fun getAppConfiguration(): AppConfiguration? {
        return tryCatchOrReturnNull { API.getAppConfiguration(API_KEY) }
    }

    override fun getNowPlayingMoviePage(page: Int): MoviePage? {
        return tryCatchOrReturnNull { API.getNowPlaying(page, API_KEY) }
    }

    override fun getPopularMoviePage(page: Int): MoviePage? {
        return tryCatchOrReturnNull { API.getPopular(page, API_KEY) }
    }

    override fun getTopRatedMoviePage(page: Int): MoviePage? {
        return tryCatchOrReturnNull { API.getTopRated(page, API_KEY) }
    }

    override fun getUpcomingMoviePage(page: Int): MoviePage? {
        return tryCatchOrReturnNull { API.getUpcoming(page, API_KEY) }
    }

    override fun getMovieDetails(movieId: Double): MovieDetail? {
        return tryCatchOrReturnNull { API.getMovieDetails(movieId, API_KEY) }
    }

    override fun performSearch(query: String, page: Int): SearchPage? {
        return tryCatchOrReturnNull { API.search(query, page, API_KEY) }
    }


    /**
     * Executes the provided [block] in a try-catch block and returns the result.
     * If the [block] fails with an exception, null is returned.
     */
    private inline fun <T : Any> tryCatchOrReturnNull(block: () -> Call<T>): T? {
        return try {
            block().execute().body()
        } catch (ex: Exception) {
            null
        }
    }

    companion object {
        const val API_KEY = BuildConfig.API_KEY
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

}