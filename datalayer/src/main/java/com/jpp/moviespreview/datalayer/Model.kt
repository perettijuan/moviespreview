package com.jpp.moviespreview.datalayer

/***************************************************************************************************
 ********** Contains all the model classes that the data layer exposes to it's clients.  ***********
 ********** All classes defined in the file are a one to one match with the objects that ***********
 ********** the API (themoviedb.org) exposes when consumed by any client.                ***********
 ***************************************************************************************************/

/**
 * Represents the general configuration of the application. Some elements of the API require some
 * knowledge of this configuration data in order to, for instance, crete the URLs that points to
 * the images stored by the API.
 * [images] - contains the configuration base path for all the images that the API supports.
 */
data class AppConfiguration(val images: ImagesConfiguration)

/**
 * Represents the configuration of the images tha the data layer can provide. For instance, a Movie
 * might have more than one image (poster, backdrop, etc.). In order to create the URL that points
 * to that image in the server, you need to use a base URL, size and the path to the file.
 * This class provides the base URL and the sizes that can be used. The path to the file is provided
 * by the object that is being used in the query (the Movie).
 * Example of a URL to an image: https://image.tmdb.org/t/p/w500/8uO0gUM8aNqYLs1OsTBQiXu0fEv.jpg
 * [base_url] - represents the base URL (https://image.tmdb.org/t/p/)
 * [poster_sizes] - represents the possible sizes for poster images (w500).
 * [profile_sizes] - represents the possible sizes for profile images (w500).
 */
data class ImagesConfiguration(val base_url: String,
                               val poster_sizes: List<String>,
                               val profile_sizes: List<String>)