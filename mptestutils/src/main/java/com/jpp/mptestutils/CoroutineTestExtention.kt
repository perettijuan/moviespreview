package com.jpp.mptestutils

import androidx.annotation.RestrictTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext

/**
 * JUnit 5 extension to swap the main dispatcher used in coroutines execution.
 */
@RestrictTo(RestrictTo.Scope.TESTS)
class CoroutineTestExtention : BeforeEachCallback, AfterEachCallback {

    @ObsoleteCoroutinesApi
    private val surrogate = newSingleThreadContext("Main")

    @ExperimentalCoroutinesApi
    @ObsoleteCoroutinesApi
    override fun beforeEach(context: ExtensionContext?) {
        Dispatchers.setMain(surrogate)
    }

    @ExperimentalCoroutinesApi
    @ObsoleteCoroutinesApi
    override fun afterEach(context: ExtensionContext?) {
        surrogate.close()
    }


}