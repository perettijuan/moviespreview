package com.jpp.mp.stubbers

import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching
import com.jpp.mpdata.api.BaseUrlChangingInterceptorProvider

private const val movieDetailsUrl = "/3/movie/419704.0"

/**
 * Stubs the movie details response.
 */
fun stubMovieDetails() {
    BaseUrlChangingInterceptorProvider.setInterceptor(LOCAL_HOST + movieDetailsUrl)
    val jsonBody = readAssetFrom("movie_details.json")
    stubFor(get(urlPathMatching(movieDetailsUrl))
            .willReturn(aResponse()
                    .withStatus(200)
                    .withBody(jsonBody)))
}

/**
 * Stubs the movie details server response with an [errorCode].
 */
fun stubMovieDetailsWitherror(errorCode: Int = 400) {
    BaseUrlChangingInterceptorProvider.setInterceptor(LOCAL_HOST + movieDetailsUrl)
    stubFor(get(urlPathMatching(movieDetailsUrl))
            .willReturn(aResponse()
                    .withStatus(errorCode)
            ))
}