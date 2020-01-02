package com.jpp.mpdata.api

import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response

/**
 * [Interceptor] implementation that can be used to intercept URL requests and redirect the
 * request to a specific url.
 * Used in testing scenarios.
 */
object BaseUrlChangingInterceptor : Interceptor {

    private var httpUrl: HttpUrl? = null

    /**
     * Call this method when a URL must intercepted and redirected to a specific direction.
     */
    fun setInterceptor(url: String) {
        httpUrl = HttpUrl.parse(url)
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        var original = chain.request()

        httpUrl?.let {
            original = original
                    .newBuilder()
                    .url(it)
                    .build()

        }

        return chain.proceed(original)
    }
}