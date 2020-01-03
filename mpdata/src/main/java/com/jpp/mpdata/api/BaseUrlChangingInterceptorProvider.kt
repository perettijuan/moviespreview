package com.jpp.mpdata.api

import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response

/**
 * [Interceptor] provided used to intercept URL requests. This provider is used mostly for
 * testing purposes and it is hiding the usage of Retrofit to the test code (it is a simple way
 * to avoid adding the Retrofit dependency to the application's Gradle files).
 */
object BaseUrlChangingInterceptorProvider {

    /**
     * Call this method when a URL must intercepted and redirected to a specific direction.
     */
    fun setInterceptor(url: String) {
        BaseUrlChangingInterceptor.setInterceptor(url)
    }

    /**
     * Get the [Interceptor] instance used by the application.
     */
    fun getInterceptor(): Interceptor = BaseUrlChangingInterceptor

    /**
     * [Interceptor] implementation that can be used to intercept URL requests and redirect the
     * request to a specific url.
     * Used in testing scenarios.
     */
    private object BaseUrlChangingInterceptor : Interceptor {
        private var shouldIntercept = false
        private val urls = mutableListOf<HttpUrl>()

        /**
         * Call this method when a URL must intercepted and redirected to a specific direction.
         */
        fun setInterceptor(url: String) {
            HttpUrl.parse(url)?.let { parsedUrl ->
                shouldIntercept = true
                urls.add(parsedUrl)
            }
        }

        override fun intercept(chain: Interceptor.Chain): Response {
            var original = chain.request()

            if (shouldIntercept) {
                for (url in urls) {
                    if (url.pathSegments() == original.url().pathSegments()) {
                        original = original
                                .newBuilder()
                                .url(url)
                                .build()
                        break
                    }
                }
            }

            return chain.proceed(original)
        }
    }
}