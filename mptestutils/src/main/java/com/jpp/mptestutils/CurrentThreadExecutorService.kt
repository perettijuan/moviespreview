package com.jpp.mptestutils

import androidx.annotation.RestrictTo
import java.util.concurrent.Executor

/**
 * Simple [Executor] implementation to execute the provided [Runnable] in the current thread.
 * For testing purposes only.
 */
@RestrictTo(RestrictTo.Scope.TESTS)
class CurrentThreadExecutorService : Executor {
    override fun execute(p0: Runnable?) {
        p0?.run()
    }
}