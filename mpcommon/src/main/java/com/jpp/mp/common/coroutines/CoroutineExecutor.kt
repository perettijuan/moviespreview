package com.jpp.mp.common.coroutines

import java.util.concurrent.Executor
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * [Executor] based on coroutines.
 * Every time [execute] is called it spawns a new coroutine in the [background] dispatcher using the provided [context].
 * This allows to cancel the execution of the command when the [context] is terminated.
 */
class CoroutineExecutor(
    private val context: CoroutineScope,
    private val background: CoroutineDispatcher
) : Executor {

    override fun execute(command: Runnable?) {
        context.launch {
            withContext(background) {
                // for debug reasons logYourThread()
                command?.run()
            }
        }
    }
}
