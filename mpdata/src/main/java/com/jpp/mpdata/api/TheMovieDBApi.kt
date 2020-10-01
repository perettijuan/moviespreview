package com.jpp.mpdata.api

import com.jpp.mpdomain.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * API definition to use with Retrofit in order to execute requests to the backend.
 */
internal interface TheMovieDBApi {

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
    fun getNowPlaying(
        @Query("page") page: Int,
        @Query("api_key") api_key: String,
        @Query("language") language: String? = null,
        @Query("region") region: String? = null
    ): Call<MoviePage>

    /**
     * Retrieves the list of most popular movies.
     * [page] the current page to retrieve.
     * [api_key] the api key provided by themoviedb.
     * [language] Pass a ISO 639-1 value to display translated data for the fields that support it. - Optional.
     * [region] Specify a ISO 3166-1 code to filter release dates. Must be uppercase. - Optional.
     */
    @GET("movie/popular")
    fun getPopular(
        @Query("page") page: Int,
        @Query("api_key") api_key: String,
        @Query("language") language: String? = null,
        @Query("region") region: String? = null
    ): Call<MoviePage>

    /**
     * Retrieves the list of top rated movies.
     * [page] the current page to retrieve.
     * [api_key] the api key provided by themoviedb.
     * [language] Pass a ISO 639-1 value to display translated data for the fields that support it. - Optional.
     * [region] Specify a ISO 3166-1 code to filter release dates. Must be uppercase. - Optional.
     */
    @GET("movie/top_rated")
    fun getTopRated(
        @Query("page") page: Int,
        @Query("api_key") api_key: String,
        @Query("language") language: String? = null,
        @Query("region") region: String? = null
    ): Call<MoviePage>

    /**
     * Retrieves the list of upcoming movies.
     * [page] the current page to retrieve.
     * [api_key] the api key provided by themoviedb.
     * [language] Pass a ISO 639-1 value to display translated data for the fields that support it. - Optional.
     * [region] Specify a ISO 3166-1 code to filter release dates. Must be uppercase. - Optional.
     */
    @GET("movie/upcoming")
    fun getUpcoming(
        @Query("page") page: Int,
        @Query("api_key") api_key: String,
        @Query("language") language: String? = null,
        @Query("region") region: String? = null
    ): Call<MoviePage>

    /**
     * Retrieves th details of a Movie identified by its personId.
     * [movieId] the identifier of the movie.
     * [api_key] the api key provided by themoviedb.
     * [language] Pass a ISO 639-1 value to display translated data for the fields that support it. - Optional.
     */
    @GET("movie/{movie_id}")
    fun getMovieDetails(
        @Path("movie_id") movieId: Double,
        @Query("api_key") api_key: String,
        @Query("language") language: String? = null
    ): Call<MovieDetail>

    /**
     * Executes a multi searchFirstPage API call.
     * [page] the current page to retrieve.
     * [api_key] the api key provided by themoviedb.
     * [language] Pass a ISO 639-1 value to display translated data for the fields that support it. - Optional.
     * [region] Specify a ISO 3166-1 code to filter release dates. Must be uppercase. - Optional.
     * [query] The query to execute.
     */
    @GET("search/multi")
    fun search(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("api_key") api_key: String,
        @Query("language") language: String? = null,
        @Query("region") region: String? = null
    ): Call<SearchPage>

    /**
     * Retrieves a [Person\] identified by its personId.
     * [personId] the identifier of the person.
     * [api_key] the api key provided by themoviedb.
     * [language] Pass a ISO 639-1 value to display translated data for the fields that support it. - Optional.
     */
    @GET("person/{person_id}")
    fun getPerson(
        @Path("person_id") personId: Double,
        @Query("api_key") api_key: String,
        @Query("language") language: String? = null
    ): Call<Person>

    /**
     * Retrieves the credits of a given movie.
     * [movieId] the identifier of the movie.
     * [api_key] the api key provided by themoviedb.
     */
    @GET("movie/{movie_id}/credits")
    fun getMovieCredits(
        @Path("movie_id") movieId: Double,
        @Query("api_key") api_key: String
    ): Call<Credits>

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
    fun createSession(@Query("api_key") api_key: String, @Body requestToken: RequestTokenBody): Call<Session>

    /**
     * Retrieves the user account data.
     * [session_id] the session identifier for the current user.
     * [api_key] the api key provided by themoviedb.
     */
    @GET("account")
    fun getUserAccount(@Query("session_id") session_id: String, @Query("api_key") api_key: String): Call<UserAccount>

    /**
     * Retrieves the movie state from the user's account perspective.
     * [movieId] the identifier of the movie.
     * [sessionId] the session identifier for the current user.
     * [api_key] the api key provided by themoviedb.
     */
    @GET("movie/{movie_id}/account_states")
    fun getMovieAccountState(
        @Path("movie_id") movieId: Double,
        @Query("session_id") sessionId: String,
        @Query("api_key") api_key: String
    ): Call<MovieState>

    /**
     * Updates the favorite state of a media resource (movie or tv).
     * [accountId] the identifier of users account.
     * [sessionId] the session identifier for the current user.
     * [api_key] the api key provided by themoviedb.
     */
    @POST("account/{account_id}/favorite")
    fun markMediaAsFavorite(
        @Path("account_id") accountId: Double,
        @Query("session_id") sessionId: String,
        @Query("api_key") api_key: String,
        @Body body: FavoriteMediaBody
    ): Call<FavoriteMediaResponse>

    /**
     * Updates the watchlist state of a media resource (movie or tv).
     * [accountId] the identifier of users account.
     * [sessionId] the session identifier for the current user.
     * [api_key] the api key provided by themoviedb.
     */
    @POST("account/{account_id}/watchlist")
    fun addMediaToWatchlist(
        @Path("account_id") accountId: Double,
        @Query("session_id") sessionId: String,
        @Query("api_key") api_key: String,
        @Body body: WatchlistMediaBody
    ): Call<WatchlistMediaResponse>

    /**
     * Rates the movie defined by [movieId].
     * [movieId] the identifier of the movie to rate.
     * [sessionId] the session identifier for the current user.
     * [api_key] the api key provided by themoviedb.
     */
    @POST("movie/{movie_id}/rating")
    fun rateMovie(
        @Path("movie_id") movieId: Double,
        @Query("session_id") sessionId: String,
        @Query("api_key") api_key: String,
        @Body body: RateMovieBody
    ): Call<RateMovieResponse>

    /**
     * Retrieves the list of favorite movies of the user.
     * [accountId] the identifier of users account.
     * [page] the current page to retrieve.
     * [sessionId] the session identifier for the current user.
     * [api_key] the api key provided by themoviedb.
     * [language] Pass a ISO 639-1 value to display translated data for the fields that support it. - Optional.
     */
    @GET("account/{account_id}/favorite/movies")
    fun getFavoriteMoviesPage(
        @Path("account_id") accountId: Double,
        @Query("page") page: Int,
        @Query("session_id") sessionId: String,
        @Query("api_key") api_key: String,
        @Query("language") language: String? = null
    ): Call<MoviePage>

    /**
     * Deletes the rating for a movie that has previously rated by the user.
     * [movieId] the identifier of the movie to rate.
     * [sessionId] the session identifier for the current user.
     * [api_key] the api key provided by themoviedb.
     */
    @DELETE("movie/{movie_id}/rating")
    fun deleteMovieRating(
        @Path("movie_id") movieId: Double,
        @Query("session_id") sessionId: String,
        @Query("api_key") api_key: String
    ): Call<RateMovieResponse>

    /**
     * Retrieves the list of rated movies of the user.
     * [accountId] the identifier of users account.
     * [page] the current page to retrieve.
     * [sessionId] the session identifier for the current user.
     * [api_key] the api key provided by themoviedb.
     * [language] Pass a ISO 639-1 value to display translated data for the fields that support it. - Optional.
     */
    @GET("account/{account_id}/rated/movies")
    fun getRatedMoviesPage(
        @Path("account_id") accountId: Double,
        @Query("page") page: Int,
        @Query("session_id") sessionId: String,
        @Query("api_key") api_key: String,
        @Query("language") language: String? = null
    ): Call<MoviePage>

    /**
     * Retrieves the page of watchlisted movies of the user.
     * [accountId] the identifier of users account.
     * [page] the current page to retrieve.
     * [sessionId] the session identifier for the current user.
     * [api_key] the api key provided by themoviedb.
     * [language] Pass a ISO 639-1 value to display translated data for the fields that support it. - Optional.
     */
    @GET("account/{account_id}/watchlist/movies")
    fun getWatchlistMoviesPage(
        @Path("account_id") accountId: Double,
        @Query("page") page: Int,
        @Query("session_id") sessionId: String,
        @Query("api_key") api_key: String,
        @Query("language") language: String? = null
    ): Call<MoviePage>

    /**
     * Retrieves the list of official genres for movies.
     * [api_key] the api key provided by themoviedb.
     * [language] Pass a ISO 639-1 value to display translated data for the fields that support it. - Optional.
     */
    @GET("genre/movie/list")
    fun getMovieGenres(
        @Query("api_key") api_key: String,
        @Query("language") language: String? = null
    ): Call<List<MovieGenre>>

    /**
     * TODO JPP add javadoc
     * Retrieves the list of movies currently playing in theaters.
     * [page] the current page to retrieve.
     * [api_key] the api key provided by themoviedb.
     * [language] Pass a ISO 639-1 value to display translated data for the fields that support it. - Optional.
     * [region] Specify a ISO 3166-1 code to filter release dates. Must be uppercase. - Optional.
     */
    @GET("discover/movie")
    fun discover(
        @Query("page") page: Int,
        @Query("api_key") api_key: String,
        @Query("language") language: String? = null,
        @Query("region") region: String? = null
    ): Call<MoviePage>
}
