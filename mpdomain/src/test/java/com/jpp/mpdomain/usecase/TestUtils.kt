package com.jpp.mpdomain.usecase

import com.jpp.mpdomain.ImagesConfiguration
import com.jpp.mpdomain.Movie

internal val imagesConfig = ImagesConfiguration(
    base_url = "baseUrl/",
    poster_sizes = listOf(
        "w92",
        "w154",
        "w185",
        "w342",
        "w500",
        "w780",
        "original"
    ),
    profile_sizes = listOf(
        "w45",
        "w185",
        "h632",
        "original"
    ),
    backdrop_sizes = listOf(
        "w300",
        "w780",
        "w1280",
        "original"
    )

)

internal val mockedMovieList: List<Movie> = mutableListOf<Movie>().apply {
    for (i in 0..50) {
        add(
            Movie(
                id = i.toDouble(),
                title = "titleRes$i",
                original_language = "oTitle$i",
                overview = "overview$i",
                release_date = "releaseDate$i",
                original_title = "originalLanguage$i",
                poster_path = "posterPath$i",
                backdrop_path = "backdropPath$i",
                vote_count = i.toDouble(),
                vote_average = i.toFloat(),
                popularity = i.toFloat()
            )
        )
    }
}

internal fun List<Movie>.getImagesConfiguredMovies(): List<Movie> {
    return toMutableList().mapIndexed { index, movie ->
        movie.copy(
            poster_path = "baseUrl/originalposterPath$index",
            backdrop_path = "baseUrl/originalbackdropPath$index"
        )
    }
}
