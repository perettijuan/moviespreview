package com.jpp.mpdata.datasources.account

import android.util.SparseArray
import com.jpp.mpdomain.MoviePage
import com.jpp.mpdomain.UserAccount

interface AccountDb {
    fun storeUserAccountInfo(userAccount: UserAccount)
    fun getUserAccountInfo(): UserAccount?
    fun getFavoriteMovies(page: Int): MoviePage?
    fun storeFavoriteMoviesPage(page: Int, moviePage: MoviePage)
    fun flushData()

    /**
     * Only in-memory caching since the info of the user should be refreshed every time
     * it is accessed.
     */
    class Impl : AccountDb {
        private var userAccountInfo: UserAccount? = null

        private val favoriteMovies = SparseArray<MoviePage>()
        override fun storeUserAccountInfo(userAccount: UserAccount) {
            userAccountInfo = userAccount
        }

        override fun getUserAccountInfo(): UserAccount? = userAccountInfo

        override fun storeFavoriteMoviesPage(page: Int, moviePage: MoviePage) {
            favoriteMovies.put(page, moviePage)
        }

        override fun getFavoriteMovies(page: Int): MoviePage? = favoriteMovies.get(page, null)

        override fun flushData() {
            favoriteMovies.clear()
        }
    }
}