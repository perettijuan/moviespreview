package com.jpp.mpdata.cache.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


/**
 * Represents an image size stored in the database. The application
 * configuration might have several lists of image size configuration
 * that are used to create an image URL. This table in the DB stores
 * each individual size.
 * An [AppConfiguration] decomposes in a list of these objects.
 */
@Entity(tableName = "image_size")
data class DBImageSize(@ColumnInfo(name = "base_url") var baseUrl: String,
                       @ColumnInfo(name = "size") var size: String,
                       @ColumnInfo(name = "image_type") val imageType: Int /* receives: poster or profile */) {
    @ColumnInfo(name = "_id")
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}

/**
 * Represents a Movie Page in the database.
 * [page] - is the page number and it is used as the primary key.
 * [dueDate] - represents the date until this page is valid.
 */
@Entity(tableName = "movie_pages")
data class DBMoviePage(@ColumnInfo(name = "page") var page: Int,
                       @ColumnInfo(name = "totalPages") var totalPages: Int,
                       @ColumnInfo(name = "totalResults") var totalResults: Int,
                       @ColumnInfo(name = "section") var section: String,
                       @ColumnInfo(name = "duedate") var dueDate: Long) {
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "_id") var id: Int = 0
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
data class DBMovie(@ColumnInfo(name = "movieId") var movieId: Double,
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
                   @ColumnInfo(name = "page_id") var pageId: Long) {
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "_id") var id: Int = 0
}
