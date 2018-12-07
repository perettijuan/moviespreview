package com.jpp.moviespreview.domainlayer.interactor

/**
 * Defines the signature of an Interactor that encapsulates a specific feature.
 * Each Interactor defined in the application takes care of executing a specific
 * piece of functionality, following the single responsibility principle.
 */
interface Interactor<in Param : Any, out Result> {
    operator fun invoke(parameter: Param? = null) : Result = execute(parameter)
    fun execute(parameter: Param?) : Result
}