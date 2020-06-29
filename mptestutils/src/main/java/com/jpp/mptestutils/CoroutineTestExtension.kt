package com.jpp.mptestutils

import androidx.annotation.RestrictTo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext

/**
 * JUnit 5 extension change the [Dispatchers.Main] dispatcher for the [TestCoroutineDispatcher]
 * provided by jetbrains.
 */
@RestrictTo(RestrictTo.Scope.TESTS)
@ExperimentalCoroutinesApi
class CoroutineTestExtension() : BeforeEachCallback, AfterEachCallback {

    override fun beforeEach(context: ExtensionContext?) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun afterEach(context: ExtensionContext?) {
        Dispatchers.resetMain()
        (testDispatcher as TestCoroutineDispatcher).cleanupTestCoroutines()
    }

    /*
     * TODO
     *  I think this is not the best way to share properties between tests and extensions
     *  in JUnit 5. I should probably add properties to the ExtensionContext, but not
     *  sure how to do it yet.
     *  For the moment, this is working for me.
     *  More information in ==> https://blog.codefx.org/design/architecture/junit-5-extension-model/#Extension-Context
     */
    companion object {
        val testDispatcher: CoroutineDispatcher = TestCoroutineDispatcher()
    }
}
