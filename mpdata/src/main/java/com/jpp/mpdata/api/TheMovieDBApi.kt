package com.jpp.mpdata.api

import com.jpp.mpdomain.*
import retrofit2.Call
import retrofit2.http.*

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

    /**
     * Retrieves th details of a Movie identified by its personId.
     * [movieId] the identifier of the movie.
     * [api_key] the api key provided by themoviedb.
     * [language] Pass a ISO 639-1 value to display translated data for the fields that support it. - Optional.
     */
    @GET("movie/{movie_id}")
    fun getMovieDetails(@Path("movie_id") movieId: Double,
                        @Query("api_key") api_key: String,
                        @Query("language") language: String? = null): Call<MovieDetail>


    /**
     * Executes a multi searchFirstPage API call.
     * [page] the current page to retrieve.
     * [api_key] the api key provided by themoviedb.
     * [language] Pass a ISO 639-1 value to display translated data for the fields that support it. - Optional.
     * [region] Specify a ISO 3166-1 code to filter release dates. Must be uppercase. - Optional.
     * [query] The query to execute.
     */
    @GET("search/multi")
    fun search(@Query("query") query: String,
               @Query("page") page: Int,
               @Query("api_key") api_key: String,
               @Query("language") language: String? = null,
               @Query("region") region: String? = null): Call<SearchPage>

    /**
     * Retrieves a [Person\] identified by its personId.
     * [personId] the identifier of the person.
     * [api_key] the api key provided by themoviedb.
     * [language] Pass a ISO 639-1 value to display translated data for the fields that support it. - Optional.
     */
    @GET("person/{person_id}")
    fun getPerson(@Path("person_id") personId: Double,
                  @Query("api_key") api_key: String,
                  @Query("language") language: String? = null): Call<Person>

    /**
     * Retrieves the credits of a given movie.
     * [movieId] the identifier of the movie.
     * [api_key] the api key provided by themoviedb.
     */
    @GET("movie/{movie_id}/credits")
    fun getMovieCredits(@Path("movie_id") movieId: Double,
                        @Query("api_key") api_key: String): Call<Credits>

    /**
     * Retrieves an [AccessToken] to be used in a login process.
     * [api_key] the api key provided by themoviedb.
     */
    @GET("authentication/token/new")
    fun getAccessToken(@Query("api_key") api_key: String): Call<AccessToken>

    /**
     * Creates a new [Session] for the provided [requestToken].
     * [api_key] the api key provided by themoviedb.
     * [requestToken] the token to use in the session creation.
     * @return a [Session] if one can be created, null any other case.
     */
    @POST("authentication/session/new")
    fun createSession(@Query("api_key") api_key: String, @Body requestToken: RequestToken): Call<Session>
}