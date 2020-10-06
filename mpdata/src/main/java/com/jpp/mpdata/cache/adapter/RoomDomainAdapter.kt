package com.jpp.mpdata.cache.adapter

import com.jpp.mpdata.cache.room.DBCastCharacter
import com.jpp.mpdata.cache.room.DBCrewPerson
import com.jpp.mpdata.cache.room.DBGenreByMovie
import com.jpp.mpdata.cache.room.DBImageSize
import com.jpp.mpdata.cache.room.DBMovie
import com.jpp.mpdata.cache.room.DBMovieDetail
import com.jpp.mpdata.cache.room.DBMovieGenre
import com.jpp.mpdata.cache.room.DBMoviePage
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
    private fun adaptDBMovieToDataMovie(dbMovie: DBMovie): Movie =
        Movie(
            id = dbMovie.movieId,
            title = dbMovie.title,
            original_title = dbMovie.originalTile,
            overview = dbMovie.overview,
            release_date = dbMovie.releaseDate,
            original_language = dbMovie.originalLanguage,
            poster_path = dbMovie.posterPath,
            backdrop_path = dbMovie.backdropPath,
            vote_count = dbMovie.voteCount,
            vote_average = dbMovie.voteAverage,
            popularity = dbMovie.popularity
        )

    /**
     * [DBMovieDetail] to [MovieDetail].
     */
    internal fun movieDetail(
        dbMovieDetail: DBMovieDetail,
        genres: List<DBGenreByMovie>
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
     * Adapts the provided [DBGenreByMovie] to the respective [MovieGenre].
     */
    private fun adaptDBMovieGenreToDataMovieGenre(dbMovieGenre: DBGenreByMovie): MovieGenre =
        MovieGenre(id = dbMovieGenre.id, name = dbMovieGenre.name)

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

    /**
     * [DBMovieGenre] to [MovieGenre].
     */
    internal fun movieGenre(genre: DBMovieGenre): MovieGenre =
        MovieGenre(id = genre.id, name = genre.name)
}
