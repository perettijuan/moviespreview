package com.jpp.mpcredits

import android.os.Bundle

/**
 * Contains utilities to perform navigation to the credits module.
 */
object NavigationCredits {

    fun navArgs(movieId: Double, movieTitle: String) = Bundle()
            .apply {
                putDouble("movieId", movieId)
                putString("movieTitle", movieTitle)
            }
}