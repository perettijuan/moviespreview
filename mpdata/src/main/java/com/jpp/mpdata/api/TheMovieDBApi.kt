package com.jpp.mpdata.api

import com.jpp.mpdomain.AppConfiguration
import com.jpp.mpdomain.MoviePage
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * API instance for Retrofit
 */
interface TheMovieDBApi {

    /**
     * Retrieves the current configuration from the server.
     */
    @GET("configuration")
    fun getAppConfiguration(@Query("api_key") api_key: String): Call<AppConfiguration>

    /**
     * Retrieves the list of movies currently playing in theaters.
     * [page] the current page to retrieve.
     * [api_key] the api key provided by themoviedb.
     * [language] Pass a ISO 639-1 value to display translated data for the fields that support it. - Optional.
     * [region] Specify a ISO 3166-1 code to filter release dates. Must be uppercase. - Optional.
     */
    @GET("movie/now_playing")
    fun getNowPlaying(@Query("page") page: Int,
                      @Query("api_key") api_key: String,
                      @Query("language") language: String? = null,
                      @Query("region") region: String? = null): Call<MoviePage>

    /**
     * Retrieves the list of most popular movies.
     * [page] the current page to retrieve.
     * [api_key] the api key provided by themoviedb.
     * [language] Pass a ISO 639-1 value to display translated data for the fields that support it. - Optional.
     * [region] Specify a ISO 3166-1 code to filter release dates. Must be uppercase. - Optional.
     */
    @GET("movie/popular")
    fun getPopular(@Query("page") page: Int,
                   @Query("api_key") api_key: String,
                   @Query("language") language: String? = null,
                   @Query("region") region: String? = null): Call<MoviePage>

    /**
     * Retrieves the list of top rated movies.
     * [page] the current page to retrieve.
     * [api_key] the api key provided by themoviedb.
     * [language] Pass a ISO 639-1 value to display translated data for the fields that support it. - Optional.
     * [region] Specify a ISO 3166-1 code to filter release dates. Must be uppercase. - Optional.
     */
    @GET("movie/top_rated")
    fun getTopRated(@Query("page") page: Int,
                    @Query("api_key") api_key: String,
                    @Query("language") language: String? = null,
                    @Query("region") region: String? = null): Call<MoviePage>

    /**
     * Retrieves the list of upcoming movies.
     * [page] the current page to retrieve.
     * [api_key] the api key provided by themoviedb.
     * [language] Pass a ISO 639-1 value to display translated data for the fields that support it. - Optional.
     * [region] Specify a ISO 3166-1 code to filter release dates. Must be uppercase. - Optional.
     */
    @GET("movie/upcoming")
    fun getUpcoming(@Query("page") page: Int,
                    @Query("api_key") api_key: String,
                    @Query("language") language: String? = null,
                    @Query("region") region: String? = null): Call<MoviePage>

}