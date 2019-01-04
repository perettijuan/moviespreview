package com.jpp.mpdata.api

import com.jpp.mpdata.BuildConfig
import com.jpp.mpdomain.MoviePage
import com.jpp.mpdomain.repository.movies.MoviesApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Represents the remote API that MoviesPreview supports. It is a wrapper
 * around Retrofit classes to provide a clean access to the API.
 */
class MPApi : MoviesApi {


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