package com.jpp.moviespreview.utiltest

import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.MoviePage
import com.jpp.mpdomain.usecase.movies.GetMoviesUseCaseResult

fun successGetMoviesUCExecution(moviesInPageCount: Int) = GetMoviesUseCaseResult.Success(createMoviesPage(1, moviesInPageCount))

fun createMoviesPage(page: Int, totalResults: Int) = MoviePage(
        page = page,
        results = createMoviesForPage(page, totalResults),
        total_pages = 10,
        total_results = 1000
)

fun moviesPages(totalPages: Int): List<MoviePage> {
    return mutableListOf<MoviePage>().apply {
        for (i in 1..totalPages) {
            add(createMoviesPage(i, 10))
        }
    }
}

private fun createMoviesForPage(page: Int, totalResults: Int = 10): List<Movie> {
    return mutableListOf<Movie>().apply {
        for (i in 1..totalResults) {
            add(Movie(
                    id = (page + i).toDouble(),
                    poster_path = "/m110vLaDDOCca4hfOcS5mK5cDke.jpg",
                    backdrop_path = "/m110vLaDDOCca4hfOcS5mK5cDke.jpg",
                    title = "Movie $i",
                    original_title = "Movie Title $i",
                    original_language = "US",
                    overview = "Overview for $i",
                    release_date = "aReleaseDate for $i",
                    vote_count = i.toDouble(),
                    vote_average = i.toFloat(),
                    popularity = i.toFloat()
            ))
        }
    }
}