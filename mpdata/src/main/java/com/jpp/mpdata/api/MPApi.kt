package com.jpp.mpdata.api

import com.google.gson.GsonBuilder
import com.jpp.mpdata.BuildConfig
import com.jpp.mpdata.datasources.account.AccountApi
import com.jpp.mpdata.datasources.configuration.ConfigurationApi
import com.jpp.mpdata.datasources.credits.CreditsApi
import com.jpp.mpdata.datasources.moviedetail.MovieDetailApi
import com.jpp.mpdata.datasources.moviepage.MoviesApi
import com.jpp.mpdata.datasources.moviestate.MovieStateApi
import com.jpp.mpdata.datasources.person.PersonApi
import com.jpp.mpdata.datasources.search.SearchApi
import com.jpp.mpdata.datasources.session.SessionApi
import com.jpp.mpdata.datasources.tokens.AccessTokenApi
import com.jpp.mpdomain.AccessToken
import com.jpp.mpdomain.AppConfiguration
import com.jpp.mpdomain.Credits
import com.jpp.mpdomain.MovieDetail
import com.jpp.mpdomain.MoviePage
import com.jpp.mpdomain.MovieState
import com.jpp.mpdomain.MovieStateRate
import com.jpp.mpdomain.Person
import com.jpp.mpdomain.SearchPage
import com.jpp.mpdomain.Session
import com.jpp.mpdomain.SupportedLanguage
import com.jpp.mpdomain.UserAccount
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Represents the remote API that MoviesPreview supports.
 * It is a wrapper around Retrofit classes to provide a clean access to the API.
 */
open class MPApi :
        ConfigurationApi,
        MoviesApi,
    SearchApi,
    PersonApi,
    CreditsApi,
        SessionApi,
        AccountApi,
        AccessTokenApi,
        MovieDetailApi,
        MovieStateApi {

    override fun getAppConfiguration(): AppConfiguration? {
        return tryCatchOrReturnNull { API.getAppConfiguration(API_KEY) }
    }

    override fun getNowPlayingMoviePage(page: Int, language: SupportedLanguage): MoviePage? {
        return tryCatchOrReturnNull { API.getNowPlaying(page, API_KEY, language.id) }
    }

    override fun getPopularMoviePage(page: Int, language: SupportedLanguage): MoviePage? {
        return tryCatchOrReturnNull { API.getPopular(page, API_KEY, language.id) }
    }

    override fun getTopRatedMoviePage(page: Int, language: SupportedLanguage): MoviePage? {
        return tryCatchOrReturnNull { API.getTopRated(page, API_KEY, language.id) }
    }

    override fun getUpcomingMoviePage(page: Int, language: SupportedLanguage): MoviePage? {
        return tryCatchOrReturnNull { API.getUpcoming(page, API_KEY, language.id) }
    }

    override fun getMovieDetails(movieId: Double, language: SupportedLanguage): MovieDetail? {
        return tryCatchOrReturnNull { API.getMovieDetails(movieId, API_KEY, language.id) }
    }

    override fun performSearch(query: String, page: Int, language: SupportedLanguage): SearchPage? {
        return tryCatchOrReturnNull { API.search(query, page, API_KEY, language.id) }
    }

    override fun getPerson(personId: Double, language: SupportedLanguage): Person? {
        return tryCatchOrReturnNull { API.getPerson(personId, API_KEY, language.id) }
    }

    override fun getCreditsForMovie(movieId: Double): Credits? {
        return tryCatchOrReturnNull { API.getMovieCredits(movieId, API_KEY) }
    }

    override fun getAccessToken(): AccessToken? {
        return tryCatchOrReturnNull { API.getAccessToken(API_KEY) }
    }

    override fun createSession(accessToken: AccessToken): Session? {
        return tryCatchOrReturnNull { API.createSession(API_KEY, RequestTokenBody(accessToken.request_token)) }
    }

    override fun getUserAccountInfo(session: Session): UserAccount? {
        return tryCatchOrReturnNull { API.getUserAccount(session.session_id, API_KEY) }
    }

    override fun updateFavoriteMovieState(movieId: Double, asFavorite: Boolean, userAccount: UserAccount, session: Session): Boolean? {
        return API.markMediaAsFavorite(
                accountId = userAccount.id,
                sessionId = session.session_id,
                api_key = API_KEY,
                body = FavoriteMediaBody(
                        media_type = "movie",
                        favorite = asFavorite,
                        media_id = movieId)
        ).let {
            it.execute().body()?.let { true }
        }
    }

    override fun updateWatchlistMovieState(movieId: Double, inWatchList: Boolean, userAccount: UserAccount, session: Session): Boolean? {
        return API.addMediaToWatchlist(
                accountId = userAccount.id,
                sessionId = session.session_id,
                api_key = API_KEY,
                body = WatchlistMediaBody(
                        media_type = "movie",
                        media_id = movieId,
                        watchlist = inWatchList
                )
        ).let {
            it.execute().body()?.let { true }
        }
    }

    override fun rateMovie(movieId: Double, rating: Float, userAccount: UserAccount, session: Session): Boolean? {
        return API.rateMovie(
                movieId = movieId,
                sessionId = session.session_id,
                api_key = API_KEY,
                body = RateMovieBody(
                        value = rating
                )
        ).let {
            it.execute().body()?.let { true }
        }
    }

    override fun deleteMovieRating(movieId: Double, session: Session): Boolean? {
        return API.deleteMovieRating(
                movieId = movieId,
                sessionId = session.session_id,
                api_key = API_KEY
        ).let {
            it.execute().body()?.let { true }
        }
    }

    override fun getMovieAccountState(movieId: Double, session: Session): MovieState? {
        return tryCatchOrReturnNull { API.getMovieAccountState(movieId, session.session_id, API_KEY) }
    }

    override fun getFavoriteMoviePage(page: Int, userAccount: UserAccount, session: Session, language: SupportedLanguage): MoviePage? {
        return tryCatchOrReturnNull { API.getFavoriteMoviesPage(userAccount.id, page, session.session_id, API_KEY, language.id) }
    }

    override fun getRatedMoviePage(page: Int, userAccount: UserAccount, session: Session, language: SupportedLanguage): MoviePage? {
        return tryCatchOrReturnNull { API.getRatedMoviesPage(userAccount.id, page, session.session_id, API_KEY, language.id) }
    }

    override fun getWatchlistMoviePage(page: Int, userAccount: UserAccount, session: Session, language: SupportedLanguage): MoviePage? {
        return tryCatchOrReturnNull { API.getWatchlistMoviesPage(userAccount.id, page, session.session_id, API_KEY, language.id) }
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

    private companion object {
        const val API_KEY = BuildConfig.API_KEY
        val API: TheMovieDBApi by lazy {
            val gson = GsonBuilder()
                    .registerTypeAdapter(MovieStateRate::class.java, MovieStateRateJsonDeserializer())
                    .create()

            Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .baseUrl(BuildConfig.API_ENDPOINT)
                    .client(buildHttpClient())
                    .build()
                    .create(TheMovieDBApi::class.java)
        }

        private fun buildHttpClient(): OkHttpClient {
            return OkHttpClient.Builder().apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(BaseUrlChangingInterceptorProvider.getInterceptor())
                    addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })
                }
            }.build()
        }
    }
}
