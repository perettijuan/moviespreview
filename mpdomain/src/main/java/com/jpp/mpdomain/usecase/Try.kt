package com.jpp.mpdomain.usecase

/**
 * Generic result class for representing the success or failure of a function call.
 *
 * From: https://github.com/bijukunjummen/kfun/blob/master/src/main/kotlin/io/kfun/Try.kt
 */
sealed class Try<out T> {

    /**
     * Represents the possible failure causes.
     */
    sealed class FailureCause {
        object NoConnectivity : FailureCause()
        object UserNotLogged : FailureCause()
        object Unknown : FailureCause()
    }

    /**
     * Represents a successful call and the [value] that was requested.
     */
    data class Success<out T>(val value: T) : Try<T>() {
        override fun getOrNull(): T? = value
    }

    /**
     * Represents the failure case and the [cause] of it.
     */
    data class Failure<out T>(val cause: FailureCause) : Try<T>() {
        override fun getOrNull(): T? = null
    }

    /**
     * returns value if a [Success] [Try] or returns null
     */
    abstract fun getOrNull(): T?
}
