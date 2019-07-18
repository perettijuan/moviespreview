package com.jpp.mpcredits

import android.os.Bundle
import com.jpp.mp.common.extensions.getDoubleOrFail
import com.jpp.mp.common.extensions.getStringOrFail

/**
 * Contains utilities to perform navigation to the credits module.
 */
object NavigationCredits {

    fun navArgs(movieId: Double, movieTitle: String) = Bundle()
            .apply {
                putDouble("movieId", movieId)
                putString("movieTitle", movieTitle)
            }

    fun movieId(args: Bundle?) = args.getDoubleOrFail("movieId")
    fun movieTitle(args: Bundle?) = args.getStringOrFail("movieTitle")
}