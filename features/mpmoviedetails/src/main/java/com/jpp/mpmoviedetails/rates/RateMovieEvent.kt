package com.jpp.mpmoviedetails.rates

import androidx.annotation.StringRes
import com.jpp.mpmoviedetails.R

/**
 * Represents the messages that can be shown to the user when the rate action is completed.
 */
internal enum class RateMovieEvent(@StringRes val messageRes: Int) {
    RATE_SUCCESS(R.string.rate_movie_success_message),
    RATE_ERROR(R.string.rate_movie_error_message),
    DELETE_SUCCESS(R.string.rate_movie_deletion_success_message),
    DELETE_ERROR(R.string.rate_movie_deletion_error_message),
    USER_NOT_LOGGED(R.string.account_need_to_login),
    ERROR_FETCHING_DATA(R.string.rate_movie_error_retrieving_data_message)
}
