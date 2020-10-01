package com.jpp.mpdata.cache.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/*
 * This file contains the entities that are needed to operate on a database level.
 * Since the database entities differ from the domain entities, we need this middle layer
 * to map the data properly. All the transformations from domain to database and viceversa
 * are executed with a proper adapter.
 */

/**
 * Represents an image size stored in the database. The application
 * configuration might have several lists of image size configuration
 * that are used to create an image URL. This table in the DB stores
 * each individual size.
 * An [AppConfiguration] decomposes in a list of these objects.
 */
@Entity(tableName = "image_size")
data class DBImageSize(
    @ColumnInfo(name = "base_url") var baseUrl: String,
    @ColumnInfo(name = "size") var size: String,
    @ColumnInfo(name = "image_type") val imageType: Int /* receives: poster or profile */,
    @ColumnInfo(name = "duedate") var dueDate: Long /* represents the date until the data is valid */
) {
    @ColumnInfo(name = "_id")
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}

/**
 * Represents a Movie Page in the database.
 * [page] - is the page number and it is used as the primary key.
 */
@Entity(tableName = "movie_pages")
data class DBMoviePage(
    @ColumnInfo(name = "page") var page: Int,
    @ColumnInfo(name = "totalPages") var totalPages: Int,
    @ColumnInfo(name = "totalResults") var totalResults: Int,
    @ColumnInfo(name = "section") var section: String,
    @ColumnInfo(name = "duedate") var dueDate: Long /* represents the date until the data is valid */
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var id: Int = 0
}

/**
 * Represents a Movie in the database.
 * [pageId] is a foreign key that references the page to which this movie belongs to.
 */
@Entity(tableName = "movies",
        foreignKeys = [(ForeignKey(entity = DBMoviePage::class,
                parentColumns = arrayOf("_id"),
                childColumns = arrayOf("page_id"),
                onDelete = ForeignKey.CASCADE))])
data class DBMovie(
    @ColumnInfo(name = "movieId") var movieId: Double,
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "original_title") var originalTile: String,
    @ColumnInfo(name = "overview") var overview: String,
    @ColumnInfo(name = "release_date") var releaseDate: String,
    @ColumnInfo(name = "original_language") var originalLanguage: String,
    @ColumnInfo(name = "poster_path") var posterPath: String?,
    @ColumnInfo(name = "backdrop_path") var backdropPath: String?,
    @ColumnInfo(name = "vote_count") var voteCount: Double,
    @ColumnInfo(name = "vote_average") var voteAverage: Float,
    @ColumnInfo(name = "popularity") var popularity: Float,
    @ColumnInfo(name = "page_id") var pageId: Long
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var id: Int = 0
}

/**
 * Represents the detail of a movie in the data base.
 */
@Entity(tableName = "movies_details")
data class DBMovieDetail(
    @PrimaryKey @ColumnInfo(name = "_id") var id: Double,
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "overview") var overview: String,
    @ColumnInfo(name = "release_date") var releaseDate: String,
    @ColumnInfo(name = "poster_path") var posterPath: String?,
    @ColumnInfo(name = "vote_count") var voteCount: Double,
    @ColumnInfo(name = "vote_average") var voteAverage: Float,
    @ColumnInfo(name = "popularity") var popularity: Float,
    @ColumnInfo(name = "duedate") var dueDate: Long /* represents the date until the data is valid */
)

/**
 * Represents a genre of a movie in the database.
 */
@Entity(tableName = "genres",
        foreignKeys = [(ForeignKey(entity = DBMovieDetail::class,
                parentColumns = arrayOf("_id"),
                childColumns = arrayOf("movie_detail_d"),
                onDelete = ForeignKey.CASCADE))])
data class DBGenreByMovie(
    @PrimaryKey @ColumnInfo(name = "_id") var id: Int,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "movie_detail_d") var movieDetailId: Double
)

/**
 * Represents a character present in a movie cast. We don't store the MovieCredits
 * class in the DB. Instead, we store the movie personId on this entity and create the MovieCredits
 * on demand. Also, the movie personId is not stored as a foreign key since we don't want to
 * delete (or deal with integrity) at this level.
 */
@Entity(tableName = "movie_cast_characters")
data class DBCastCharacter(
    @PrimaryKey @ColumnInfo(name = "_id") var id: Double,
    @ColumnInfo(name = "character") var character: String,
    @ColumnInfo(name = "credit_id") var creditId: String,
    @ColumnInfo(name = "gender") var gender: Int,
    @ColumnInfo(name = "person_id") var personId: Double,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "order") var order: Int,
    @ColumnInfo(name = "profile_path") var profilePath: String?,
    @ColumnInfo(name = "movie_id") var movieId: Double,
    @ColumnInfo(name = "duedate") var dueDate: Long /* represents the date until the data is valid */
) // -> this represents the ID of the movie to which this cast belongs to.
// We do not store it as a foreign key since we don't want to delete it on CASCADE and we don't want to deal with integrity at this level.

/**
 * Represents a crew person present in a movie cast. We don't store the MovieCredits
 * class in the DB. Instead, we store the movie personId on this entity and create the MovieCredits
 * on demand. Also, the movie personId is not stored as a foreign key since we don't want to
 * delete (or deal with integrity) at this level.
 */
@Entity(tableName = "movie_crew_person")
data class DBCrewPerson(
    @PrimaryKey @ColumnInfo(name = "_id") var id: Double,
    @ColumnInfo(name = "department") var department: String,
    @ColumnInfo(name = "gender") var gender: Int,
    @ColumnInfo(name = "credit_id") var creditId: String,
    @ColumnInfo(name = "executionJob") var job: String,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "profile_path") var profilePath: String?,
    @ColumnInfo(name = "movie_id") var movieId: Double,
    @ColumnInfo(name = "duedate") var dueDate: Long /* represents the date until the data is valid */
) // -> this represents the ID of the movie to which this cast belongs to.
// We do not store it as a foreign key since we don't want to delete it on CASCADE and we don't want to deal with integrity at this level.

/**
 * Represents an official genre for movies.
 */
@Entity(tableName = "movie_genres")
data class DBMovieGenre(
    @PrimaryKey @ColumnInfo(name = "_id") var id: Int,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "duedate") var dueDate: Long /* represents the date until the data is valid */
)