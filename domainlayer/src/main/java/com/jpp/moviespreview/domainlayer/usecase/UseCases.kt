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
