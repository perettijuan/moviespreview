package com.jpp.mpdata.cache.adapter

import com.jpp.mp.common.extensions.addAllMapping
import com.jpp.mpdata.cache.room.DBCastCharacter
import com.jpp.mpdata.cache.room.DBCrewPerson
import com.jpp.mpdata.cache.room.DBImageSize
import com.jpp.mpdata.cache.room.DBMovie
import com.jpp.mpdata.cache.room.DBMovieDetail
import com.jpp.mpdata.cache.room.DBGenreByMovie
import com.jpp.mpdata.cache.room.DBMoviePage
import com.jpp.mpdata.cache.room.ImageTypes
import com.jpp.mpdomain.AppConfiguration
import com.jpp.mpdomain.CastCharacter
import com.jpp.mpdomain.CrewMember
import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.MovieDetail
import com.jpp.mpdomain.MovieGenre
import com.jpp.mpdomain.MoviePage

class DomainRoomAdapter {

    /**
     * [MoviePage] to [DBMoviePage].
     */
    internal fun moviePage(
        dataMoviePage: MoviePage,
        sectionName: String,
        dueDate: Long
    ): DBMoviePage = DBMoviePage(
        page = dataMoviePage.page,
        totalPages = dataMoviePage.total_pages,
        totalResults = dataMoviePage.total_results,
        section = sectionName,
        dueDate = dueDate
    )

    /**
     * [Movie] to [DBMovie].
     */
    internal fun movie(dataMovie: Movie, pageId: Long): DBMovie = DBMovie(
        movieId = dataMovie.id,
        title = dataMovie.title,
        originalTile = dataMovie.original_title,
        overview = dataMovie.overview,
        releaseDate = dataMovie.release_date,
        originalLanguage = dataMovie.original_language,
        posterPath = dataMovie.poster_path,
        backdropPath = dataMovie.backdrop_path,
        voteCount = dataMovie.vote_count,
        voteAverage = dataMovie.vote_average,
        popularity = dataMovie.popularity,
        pageId = pageId
    )

    /**
     * [MovieDetail] to [DBMovieDetail].
     */
    internal fun movieDetail(
        dataMovieDetail: MovieDetail,
        dueDate: Long
    ): DBMovieDetail = DBMovieDetail(
        id = dataMovieDetail.id,
        title = dataMovieDetail.title,
        overview = dataMovieDetail.overview,
        releaseDate = dataMovieDetail.release_date,
        posterPath = dataMovieDetail.poster_path,
        voteCount = dataMovieDetail.vote_count,
        voteAverage = dataMovieDetail.vote_average,
        popularity = dataMovieDetail.popularity,
        dueDate = dueDate
    )

    /**
     * [MovieGenre] to [DBGenreByMovie].
     */
    internal fun genre(
        dataMovieGenre: MovieGenre,
        detailId: Double
    ): DBGenreByMovie = DBGenreByMovie(
        id = dataMovieGenre.id,
        name = dataMovieGenre.name,
        movieDetailId = detailId
    )

    /**
     * [CastCharacter] to [DBCastCharacter].
     */
    internal fun castCharacter(
        castCharacter: CastCharacter,
        movieId: Double,
        dueDate: Long
    ): DBCastCharacter = DBCastCharacter(
        id = castCharacter.cast_id,
        character = castCharacter.character,
        creditId = castCharacter.credit_id,
        gender = castCharacter.gender,
        personId = castCharacter.id,
        name = castCharacter.name,
        order = castCharacter.order,
        profilePath = castCharacter.profile_path,
        movieId = movieId,
        dueDate = dueDate
    )

    /**
     * [CrewMember] to [DBCrewPerson].
     */
    internal fun crewPerson(
        crewMember: CrewMember,
        movieId: Double,
        dueDate: Long
    ): DBCrewPerson = DBCrewPerson(
        id = crewMember.id,
        department = crewMember.department,
        gender = crewMember.gender,
        creditId = crewMember.credit_id,
        job = crewMember.job,
        name = crewMember.name,
        profilePath = crewMember.profile_path,
        movieId = movieId,
        dueDate = dueDate
    )

    /**
     * [AppConfiguration] to a list of [DBImageSize].
     */
    internal fun imageSizes(
        appConfiguration: AppConfiguration,
        dueDate: Long
    ): List<DBImageSize> {
        return appConfiguration.images.poster_sizes
            .map {
                DBImageSize(
                    baseUrl = appConfiguration.images.base_url,
                    size = it,
                    imageType = ImageTypes.PosterType.id, dueDate = dueDate
                )
            }
            .toMutableList()
            .addAllMapping {
                appConfiguration.images.profile_sizes.map { posterSize ->
                    DBImageSize(
                        baseUrl = appConfiguration.images.base_url,
                        size = posterSize,
                        imageType = ImageTypes.ProfileType.id, dueDate = dueDate
                    )
                }
            }
            .toMutableList()
            .addAllMapping {
                appConfiguration.images.backdrop_sizes.map { backdropSize ->
                    DBImageSize(
                        baseUrl = appConfiguration.images.base_url,
                        size = backdropSize,
                        imageType = ImageTypes.BackdropType.id, dueDate = dueDate
                    )
                }
            }
    }
}
