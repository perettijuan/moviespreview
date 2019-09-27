package com.jpp.mpdata.cache.room

import com.jpp.mp.common.extensions.addAllMapping
import com.jpp.mpdomain.*

/**
 * Since the database entities differ from the domain entities, we need this middle layer
 * to map the data properly. This adapter provides the capabilities to map the domain entities
 * to database entities and viceversa.
 */
class RoomModelAdapter {

    /**
     * Adapts the provided [appConfiguration] to a list of [DBImageSize].
     */
    fun adaptAppConfigurationToImageSizes(appConfiguration: AppConfiguration, dueDate: Long): List<DBImageSize> {
        return appConfiguration.images.poster_sizes
                .map {
                    DBImageSize(baseUrl = appConfiguration.images.base_url,
                            size = it,
                            imageType = ImageTypes.PosterType.id, dueDate = dueDate)
                }
                .toMutableList()
                .addAllMapping {
                    appConfiguration.images.profile_sizes.map { posterSize ->
                        DBImageSize(baseUrl = appConfiguration.images.base_url,
                                size = posterSize,
                                imageType = ImageTypes.ProfileType.id, dueDate = dueDate)
                    }
                }
                .toMutableList()
                .addAllMapping {
                    appConfiguration.images.backdrop_sizes.map { backdropSize ->
                        DBImageSize(baseUrl = appConfiguration.images.base_url,
                                size = backdropSize,
                                imageType = ImageTypes.BackdropType.id, dueDate = dueDate)
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
     * Adapts the provided [MoviePage] to a [DBMoviePage] with the same data.
     */
    fun adaptDataMoviePageToDBMoviePage(dataMoviePage: MoviePage, sectionName: String, dueDate: Long): DBMoviePage = with(dataMoviePage) {
        DBMoviePage(
                page = page,
                totalPages = total_pages,
                totalResults = total_results,
                section = sectionName,
                dueDate = dueDate
        )
    }

    /**
     * Adapts the provided [Movie] to a [DBMovie] with the respective data.
     */
    fun adaptDataMovieToDBMovie(dataMovie: Movie, pageId: Long): DBMovie = with(dataMovie) {
        DBMovie(
                movieId = id,
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

    /**
     * Adapts the provided [MovieDetail] to a [DBMovieDetail] with the respective data.
     */
    fun adaptDataMovieDetailToDBMovieDetail(dataMovieDetail: MovieDetail, dueDate: Long): DBMovieDetail = with(dataMovieDetail) {
        DBMovieDetail(
                id = id,
                title = title,
                overview = overview,
                releaseDate = release_date,
                posterPath = poster_path,
                voteCount = vote_count,
                voteAverage = vote_average,
                popularity = popularity,
                dueDate = dueDate
        )
    }

    /**
     * Adapts the provided [MovieGenre] to a [DBMovieGenre] with the respective data.
     */
    fun adaptDataMovieGenreToDBMovieGenre(dataMovieGenre: MovieGenre, detailId: Double): DBMovieGenre = with(dataMovieGenre) {
        DBMovieGenre(
                id = id,
                name = name,
                movieDetailId = detailId
        )
    }

    /**
     * Adapts the provided [DBMovieDetail] to the respective [MovieDetail].
     */
    fun adaptDBMovieDetailToDataMovieDetail(dbMovieDetail: DBMovieDetail, genres: List<DBMovieGenre>): MovieDetail = with(dbMovieDetail) {
        MovieDetail(
                id = id,
                title = title,
                overview = overview,
                release_date = releaseDate,
                poster_path = posterPath,
                genres = genres.map { adaptDBMovieGenreToDataMovieGenre(it) },
                vote_count = voteCount,
                vote_average = voteAverage,
                popularity = popularity
        )
    }


    /**
     * Adapts the provided [DBMovieGenre] to the respective [MovieGenre].
     */
    private fun adaptDBMovieGenreToDataMovieGenre(dbMovieGenre: DBMovieGenre): MovieGenre = with(dbMovieGenre) {
        MovieGenre(
                id = id,
                name = name
        )
    }

    /**
     * Adapts a list of [CastCharacter] to a list of [DBCastCharacter].
     */
    fun adaptDomainCastCharacterListToDB(castCharacterList: List<CastCharacter>, movieId: Double, dueDate: Long): List<DBCastCharacter> =
            castCharacterList.map {
                DBCastCharacter(
                        id = it.cast_id,
                        character = it.character,
                        creditId = it.credit_id,
                        gender = it.gender,
                        personId = it.id,
                        name = it.name,
                        order = it.order,
                        profilePath = it.profile_path,
                        movieId = movieId,
                        dueDate = dueDate
                )
            }

    /**
     * Adapts a list of [CrewMember] to a list of [DBCrewPerson].
     */
    fun adaptDomainCrewMemberListToDB(crewMembers: List<CrewMember>, movieId: Double, dueDate: Long): List<DBCrewPerson> =
            crewMembers.map {
                DBCrewPerson(
                        id = it.id,
                        department = it.department,
                        gender = it.gender,
                        creditId = it.credit_id,
                        job = it.job,
                        name = it.name,
                        profilePath = it.profile_path,
                        movieId = movieId,
                        dueDate = dueDate
                )
            }

    /**
     * Adapts the provided [castCharacters] and [crewMembers] to a [Credits] instance with the provided [creditId].
     */
    fun adaptDBCreditsToDomain(castCharacters: List<DBCastCharacter>, crewMembers: List<DBCrewPerson>, creditId: Double): Credits =
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


    private companion object {
        sealed class ImageTypes(val id: Int) {
            object PosterType : ImageTypes(11)
            object ProfileType : ImageTypes(22)
            object BackdropType : ImageTypes(33)
        }
    }
}