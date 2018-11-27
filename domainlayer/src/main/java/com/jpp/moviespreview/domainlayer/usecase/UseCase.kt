package com.jpp.moviespreview.domainlayer.usecase

/**
 * Defines the signature of a UseCase that encapsulates a specific feature.
 * Each UseCase defined in the application takes care of executing a specific
 * piece of functionality, following the single responsibility principle.
 */
interface UseCase<in Param : Any, out Result> {
    operator fun invoke(parameter: Param? = null) : Result = execute(parameter)
    fun execute(parameter: Param?) : Result
}