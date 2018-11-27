package com.jpp.moviespreview.datalayer.db.room

import androidx.room.ColumnInfo
import androidx.room.Entity
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