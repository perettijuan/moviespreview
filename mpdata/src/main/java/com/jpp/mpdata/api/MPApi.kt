package com.jpp.mpdata.api

import com.jpp.mpdata.BuildConfig
import com.jpp.mpdata.repository.account.AccountApi
import com.jpp.mpdata.repository.session.SessionApi
import com.jpp.mpdata.repository.configuration.ConfigurationApi
import com.jpp.mpdata.repository.credits.CreditsApi
import com.jpp.mpdata.repository.movies.MoviesApi
import com.jpp.mpdata.repository.person.PersonApi
import com.jpp.mpdata.repository.search.SearchApi
import com.jpp.mpdomain.*
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
        SearchApi,
        PersonApi,
        CreditsApi,
        SessionApi,
        AccountApi {

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

    override fun updateMovieFavoriteState(movieId: Double, asFavorite: Boolean, userAccount: UserAccount, session: Session): Boolean? {
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

    override fun getMovieAccountState(movieId: Double, session: Session): MovieAccountState? {
        return tryCatchOrReturnNull { API.getMovieAccountState(movieId, session.session_id, API_KEY) }
    }

    override fun getFavoriteMovies(page: Int, userAccount: UserAccount, session: Session, language: SupportedLanguage): MoviePage? {
        return tryCatchOrReturnNull { API.getFavoriteMoviesPage(userAccount.id, page, session.session_id, language.id) }
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