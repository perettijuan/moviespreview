package com.jpp.moviespreview

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import io.mockk.every
import io.mockk.mockk

/**
 * @return a [LifecycleOwner] that is already resumed. Utility
 * to use on LiveData verifications.
 */
fun resumedLifecycleOwner(): LifecycleOwner {
    val lifecycleOwner: LifecycleOwner = mockk()
    val lifecycle = LifecycleRegistry(lifecycleOwner)
    every { lifecycleOwner.lifecycle } returns lifecycle
    lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    return lifecycleOwner
}