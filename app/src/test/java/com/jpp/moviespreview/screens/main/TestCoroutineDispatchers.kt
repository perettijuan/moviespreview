package com.jpp.moviespreview.screens.main

import com.jpp.moviespreview.screens.CoroutineDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class TestCoroutineDispatchers : CoroutineDispatchers {
    override fun main(): CoroutineDispatcher = Dispatchers.Unconfined
    override fun default(): CoroutineDispatcher = Dispatchers.Unconfined
}