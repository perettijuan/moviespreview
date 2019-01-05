package com.jpp.mpdata.cache.room

import com.jpp.moviespreview.common.extensions.addAllMapping
import com.jpp.mpdomain.AppConfiguration
import com.jpp.mpdomain.ImagesConfiguration
import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.MoviePage
import javax.inject.Inject

class RoomModelAdapter @Inject constructor() {

    /**
     * Adapts the provided [appConfiguration] to a list of [DBImageSize].
     */
    fun adaptAppConfigurationToImageSizes(appConfiguration: AppConfiguration): List<DBImageSize> {
        return appConfiguration.images.poster_sizes
                .map {
                    DBImageSize(baseUrl = appConfiguration.images.base_url,
                            size = it,
                            imageType = ImageTypes.PosterType.id)
                }
                .toMutableList()
                .addAllMapping {
                    appConfiguration.images.profile_sizes.map { posterSize ->
                        DBImageSize(baseUrl = appConfiguration.images.base_url,
                                size = posterSize,
                                imageType = ImageTypes.ProfileType.id)
                    }
                }
                .toMutableList()
                .addAllMapping {
                    appConfiguration.images.backdrop_sizes.map { backdropSize ->
                        DBImageSize(baseUrl = appConfiguration.images.base_url,
                                size = backdropSize,
                                imageType = ImageTypes.BackdropType.id)
                    }
                }
    }

    /**
     * Creates an [AppConfiguration] instance using the provided [dbSizes].
     */
    fun adaptImageSizesToAppConfiguration(dbSizes: List<DBImageSize>): AppConfiguration {
        return AppConfiguration(ImagesConfiguration(
                base_url = dbSizes[0].baseUrl,
                poster_sizes = dbSizes
                        .filter { it.imageType == ImageTypes.PosterType.id }
                        .map { it.size },
                profile_sizes = dbSizes
                        .filter { it.imageType == ImageTypes.ProfileType.id }
                        .map { it.size },
                backdrop_sizes = dbSizes
                        .filter { it.imageType == ImageTypes.BackdropType.id }
                        .map { it.size }
        ))
    }

    /**
     * Adapts the provided [DBMoviePage] to a data layer [MoviePage] with the same data.
     */
    fun adaptDBMoviePageToDataMoviePage(dbMoviePage: DBMoviePage, movies: List<DBMovie>): MoviePage = with(dbMoviePage) {
        MoviePage(
                page = page,
                results = movies.map { adaptDBMovieToDataMovie(it) },
                total_pages = totalPages,
                total_results = totalResults
        )
    }

    /**
     * Adapts the provided [DBMovie] to a data layer [Movie] with the same data.
     */
    private fun adaptDBMovieToDataMovie(dbMovie: DBMovie): Movie = with(dbMovie) {
        Movie(
                id = id,
                title = title,
                original_title = originalTile,
                overview = overview,
                release_date = releaseDate,
                original_language = originalLanguage,
                poster_path = posterPath,
                backdrop_path = backdropPath,
                vote_count = voteCount,
                vote_average = voteAverage,
                popularity = popularity
        )
    }

    /**
     * Adapts the provided [MoviePage] to a [DBMoviePage] with the same data.
     */
    fun adaptDataMoviePageToDBMoviePage(dataMoviePage: MoviePage): DBMoviePage = with(dataMoviePage) {
        DBMoviePage(
                page = page,
                totalPages = total_pages,
                totalResults = total_results
        )
    }

    /**
     * Adapts the provided [Movie] to a [DBMovie] with the respective data.
     */
    fun adaptDataMovieToDBMovie(dataMovie: Movie, pageId: Int): DBMovie = with(dataMovie) {
        DBMovie(
                id = id,
                title = title,
                originalTile = original_title,
                overview = overview,
                releaseDate = release_date,
                originalLanguage = original_language,
                posterPath = poster_path,
                backdropPath = backdrop_path,
                voteCount = vote_count,
                voteAverage = vote_average,
                popularity = popularity,
                pageId = pageId
        )
    }

    private companion object {
        sealed class ImageTypes(val id: Int) {
            object PosterType : ImageTypes(11)
            object ProfileType : ImageTypes(22)
            object BackdropType : ImageTypes(33)
        }
    }
}