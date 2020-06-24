package com.jpp.mpaccount.account

/**
 * Provides navigation for the user account section.
 */
interface UserAccountNavigator {
    fun navigateToLogin()
    fun navigateToFavorites()
    fun navigateToRated()
    fun navigateToWatchList()
}