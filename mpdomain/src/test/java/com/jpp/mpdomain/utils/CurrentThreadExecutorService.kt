package com.jpp.mpdomain.utils

import java.util.concurrent.Executor

/**
 * Simple [Executor] implementation to execute the provided [Runnable] in the current thread.
 * For testing purposes only.
 */
class CurrentThreadExecutorService : Executor {
    override fun execute(p0: Runnable?) {
        p0?.run()
    }
}