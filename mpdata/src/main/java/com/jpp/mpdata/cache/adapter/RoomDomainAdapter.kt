package com.jpp.mpdata.cache.adapter

import com.jpp.mpdata.cache.room.*
import com.jpp.mpdata.cache.room.ImageTypes
import com.jpp.mpdomain.AppConfiguration
import com.jpp.mpdomain.CastCharacter
import com.jpp.mpdomain.Credits
import com.jpp.mpdomain.CrewMember
import com.jpp.mpdomain.ImagesConfiguration
import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.MovieDetail
import com.jpp.mpdomain.MovieGenre
import com.jpp.mpdomain.MoviePage

/**
 * Since the database entities differ from the domain entities, we need this middle layer
 * to map the data properly. This adapter provides the capabilities to map the domain entities
 * to database entities and viceversa.
 */
class RoomDomainAdapter {

    /**
     * [DBImageSize] to [AppConfiguration]
     */
    internal fun adaptImageSizesToAppConfiguration(dbSizes: List<DBImageSize>): AppConfiguration {
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
     * [DBMoviePage] to [MoviePage].
     */
    internal fun moviePage(dbMoviePage: DBMoviePage, movies: List<DBMovie>): MoviePage = MoviePage(
        page = dbMoviePage.page,
        results = movies.map { adaptDBMovieToDataMovie(it) },
        total_pages = dbMoviePage.totalPages,
        total_results = dbMoviePage.totalResults
    )

    /**
     * Adapts the provided [DBMovie] to a data layer [Movie] with the same data.
     */
    private fun adaptDBMovieToDataMovie(dbMovie: DBMovie): Movie = with(dbMovie) {
        Movie(
            id = movieId,
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
     * [DBMovieDetail] to [MovieDetail].
     */
    internal fun movieDetail(
        dbMovieDetail: DBMovieDetail,
        genres: List<DBMovieGenre>
    ): MovieDetail = MovieDetail(
        id = dbMovieDetail.id,
        title = dbMovieDetail.title,
        overview = dbMovieDetail.overview,
        release_date = dbMovieDetail.releaseDate,
        poster_path = dbMovieDetail.posterPath,
        genres = genres.map { adaptDBMovieGenreToDataMovieGenre(it) },
        vote_count = dbMovieDetail.voteCount,
        vote_average = dbMovieDetail.voteAverage,
        popularity = dbMovieDetail.popularity
    )

    /**
     * Adapts the provided [DBMovieGenre] to the respective [MovieGenre].
     */
    private fun adaptDBMovieGenreToDataMovieGenre(dbMovieGenre: DBMovieGenre): MovieGenre =
        with(dbMovieGenre) {
            MovieGenre(
                id = id,
                name = name
            )
        }

    /**
     * [DBCastCharacter] and [DBCrewPerson] to [Credits].
     */
    internal fun credits(
        castCharacters: List<DBCastCharacter>,
        crewMembers: List<DBCrewPerson>,
        creditId: Double
    ): Credits =
        Credits(
            id = creditId,
            cast = castCharacters.map {
                CastCharacter(
                    cast_id = it.id,
                    character = it.character,
                    credit_id = it.creditId,
                    gender = it.gender,
                    id = it.personId,
                    name = it.name,
                    order = it.order,
                    profile_path = it.profilePath
                )
            },
            crew = crewMembers.map {
                CrewMember(
                    credit_id = it.creditId,
                    department = it.department,
                    gender = it.gender,
                    id = it.id,
                    job = it.job,
                    name = it.name,
                    profile_path = it.profilePath
                )
            }
        )
}
