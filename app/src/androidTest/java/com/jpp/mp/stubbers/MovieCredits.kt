package com.jpp.mp.stubbers

import com.github.tomakehurst.wiremock.client.WireMock
import com.jpp.mpdata.api.BaseUrlChangingInterceptorProvider

private const val movieCreditsUrl = "/3/movie/419704.0/credits"

/**
 * Stubs the movie credits response.
 */
fun stubMovieCredits() {
    BaseUrlChangingInterceptorProvider.setInterceptor(LOCAL_HOST + movieCreditsUrl)
    val jsonBody = readAssetFrom("movie_credits.json")
    WireMock.stubFor(WireMock.get(WireMock.urlPathMatching(movieCreditsUrl))
            .willReturn(WireMock.aResponse()
                    .withStatus(200)
                    .withBody(jsonBody)))
}

/**
 * Stubs the movie credits server response with an [errorCode].
 */
fun stubMovieCreditsWithError(errorCode: Int = 400) {
    BaseUrlChangingInterceptorProvider.setInterceptor(LOCAL_HOST + movieCreditsUrl)
    WireMock.stubFor(WireMock.get(WireMock.urlPathMatching(movieCreditsUrl))
            .willReturn(WireMock.aResponse()
                    .withStatus(errorCode)
            ))
}