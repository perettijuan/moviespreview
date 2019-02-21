package com.jpp.moviespreview.screens

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class EspressoTestCoroutineDispatchers : CoroutineDispatchers {
    override fun main(): CoroutineDispatcher = Dispatchers.Unconfined
    override fun default(): CoroutineDispatcher = Dispatchers.Unconfined
}