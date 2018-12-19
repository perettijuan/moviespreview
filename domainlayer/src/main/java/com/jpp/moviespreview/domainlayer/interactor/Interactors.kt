package com.jpp.moviespreview.domainlayer.interactor

/***************************************************************************************************
 ******* Contains the definition of all the Interactors that the application can execute.  *********
 ***************************************************************************************************/


/**
 * [Interactor] to configure the application. It will retrieve all configurations needed
 * to the normal execution of the application and it stores those configurations to be
 * used later.
 *
 * [EmptyParam] is to respect the [Interactor] definition.
 * [ConfigureApplicationResult] only indicates the success or failed execution.
 */
interface ConfigureApplicationInteractor : Interactor<EmptyParam, ConfigureApplicationResult>


/**
 * [Interactor] to retrieve a given movie page.
 *
 * [MoviePageParam] indicates the page to retrieve.
 * [MoviePageResult] represents the result of the execution.
 */
interface GetMoviePageInteractor : Interactor<MoviePageParam, MoviePageResult>


/**
 * [Interactor] to configure the images path of a given Movie.
 * By default, a Movie contains the a piece of the path that points to the images of the Movie.
 * This interactor takes care of creating the full path (URL) to the image in order to be downloaded.
 * If the execution of the interactor fails, the Movie returned contains the original path.
 *
 * [MovieImagesParam] indicates the Movie and the target sizes.
 * [MovieImagesResult] contains the Movie already configured.
 */
interface ConfigureMovieImagesInteractor : Interactor<MovieImagesParam, MovieImagesResult>


/**
 * [Interactor] to retrieve a movie page of already configured movies. This interactor takes as input
 * parameters, besides the page number and the section, the target image sizes (both backdrop and
 * poster) and configures the paths of the Movie to have the corresponding URL.
 *
 * THIS IS A VERY SPECIAL CASE WHERE WE COMBINE FUNCTIONALITY ENCAPSULATED IN SMALLER INTERACTORS IN
 * AN INTERACTOR OF GENERAL PURPOSE IN ORDER TO SIMPLIFY THE CLIENT CODE.
 *
 * [ConfiguredMoviePageParam] indicates the page number to retrieve, the section and the image sizes
 * to configure the images path of the movies.
 * [ConfiguredMoviePageResult] when successful, contains the Movies already configured.
 */
interface GetConfiguredMoviePage : Interactor<ConfiguredMoviePageParam, ConfiguredMoviePageResult>