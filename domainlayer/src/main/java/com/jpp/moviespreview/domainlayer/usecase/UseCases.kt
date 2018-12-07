package com.jpp.moviespreview.domainlayer.usecase

/***************************************************************************************************
 ******** Contains the definition of all the Use Cases that the application can execute.  **********
 ***************************************************************************************************/


/**
 * [UseCase] to configure the application. It will retrieve all configurations needed
 * to the normal execution of the application and it stores those configurations to be
 * used later.
 *
 * [EmptyParam] is to respect the [UseCase] definition.
 * [ConfigureApplicationResult] only indicates the success or failed execution.
 */
interface ConfigureApplicationUseCase : UseCase<EmptyParam, ConfigureApplicationResult>


/**
 * [UseCase] to retrieve a given movie page.
 *
 * [MoviePageParam] indicates the page to retrieve.
 * [MoviePageResult] represents the result of the execution.
 */
interface GetMoviePageUseCase : UseCase<MoviePageParam, MoviePageResult>


/**
 * [UseCase] to configure the images path of a given Movie.
 * By default, a Movie contains the a piece of the path that points to the images of the Movie.
 * This use case takes care of creating the full path (URL) to the image in order to be downloaded.
 * If the execution of the use case fails, the Movie returned contains the original path.
 *
 * [MovieImagesParam] indicates the Movie and the target sizes.
 * [MovieImagesResult] contains the Movie already configured.
 */
interface ConfigureMovieImagesUseCase : UseCase<MovieImagesParam, MovieImagesResult>