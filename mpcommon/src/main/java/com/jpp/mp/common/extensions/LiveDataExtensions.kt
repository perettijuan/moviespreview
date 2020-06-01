package com.jpp.mp.common.extensions

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

/**
 * Convenient extension to observer the values posted in a [LiveData].
 */
fun <T> LiveData<T>.observeValue(owner: LifecycleOwner, observer: (t: T) -> Unit) {
    this.observe(owner, Observer { newVal: T ->
        newVal?.let(observer)
    })
}