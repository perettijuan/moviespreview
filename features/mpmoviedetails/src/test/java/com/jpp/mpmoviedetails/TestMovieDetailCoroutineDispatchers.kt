package com.jpp.mpmoviedetails

import com.jpp.mp.common.coroutines.CoroutineDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class TestMovieDetailCoroutineDispatchers : CoroutineDispatchers {
    override fun main(): CoroutineDispatcher = Dispatchers.Unconfined
    override fun default(): CoroutineDispatcher = Dispatchers.Unconfined
}
