package com.jpp.mptestutils

import androidx.annotation.RestrictTo
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import io.mockk.every
import io.mockk.mockk

/**
 * @return a [LifecycleOwner] that is already resumed. Utility
 * to use on LiveData verifications.
 */
@RestrictTo(RestrictTo.Scope.TESTS)
fun resumedLifecycleOwner(): LifecycleOwner {
    val lifecycleOwner: LifecycleOwner = mockk()
    val lifecycle = LifecycleRegistry(lifecycleOwner)
    every { lifecycleOwner.lifecycle } returns lifecycle
    lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    return lifecycleOwner
}

/**
 * Observes the LiveData with a resumed lifecycle and calls the [observer]
 * whenever it is updated.
 */
@RestrictTo(RestrictTo.Scope.TESTS)
fun <T> LiveData<T>.observeWith(observer: (T) -> Unit) {
    observe(resumedLifecycleOwner(), Observer {
        observer(it)
    })
}
