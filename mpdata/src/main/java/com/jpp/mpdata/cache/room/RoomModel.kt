package com.jpp.mpdata.cache.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Represents a Movie Page in the database.
 * [page] - is the page number and it is used as the primary key.
 */
@Entity(tableName = "movie_pages")
data class DBMoviePage(@PrimaryKey @ColumnInfo(name = "_id") var page: Int,
                       @ColumnInfo(name = "totalPages") var totalPages: Int,
                       @ColumnInfo(name = "totalResults") var totalResults: Int)


/**
 * Represents a Movie in the database.
 * [pageId] is a foreign key that references the page to which this movie belongs to.
 */
@Entity(tableName = "movies",
        foreignKeys = [(ForeignKey(entity = DBMoviePage::class,
                parentColumns = arrayOf("_id"),
                childColumns = arrayOf("page_id"),
                onDelete = ForeignKey.CASCADE))])
data class DBMovie(@PrimaryKey @ColumnInfo(name = "_id") var id: Double,
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
                   @ColumnInfo(name = "page_id") var pageId: Int)
