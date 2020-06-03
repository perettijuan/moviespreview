package com.jpp.mpmoviedetails

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * The initialization parameter for the [MovieDetailsViewModel.onInit] method.
 */
@Parcelize
internal data class MovieDetailsParam(
    val movieId: Double,
    val movieTitle: String,
    val movieImageUrl: String
) : Parcelable