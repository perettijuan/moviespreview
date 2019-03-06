package com.jpp.moviespreview.screens

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Provides the [CoroutineDispatcher] to run coroutines in.
 * Mainly for testing purposes.
 */
interface CoroutineDispatchers {
    fun main(): CoroutineDispatcher
    fun default(): CoroutineDispatcher
}


class CoroutineDispatchersImpl : CoroutineDispatchers {
    override fun main(): CoroutineDispatcher = Dispatchers.Main
    override fun default(): CoroutineDispatcher = Dispatchers.Default
}