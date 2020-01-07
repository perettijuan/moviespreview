package com.jpp.mp.stubbers

import com.github.tomakehurst.wiremock.client.WireMock
import com.jpp.mpdata.api.BaseUrlChangingInterceptorProvider

private const val movieDetailsUrl = "/3/movie/419704.0"

/**
 * Stubs the movie details response.
 */
fun stubMovieDetails() {
    BaseUrlChangingInterceptorProvider.setInterceptor(LOCAL_HOST + movieDetailsUrl)
    val jsonBody = readAssetFrom("movie_details.json")
    WireMock.stubFor(WireMock.get(WireMock.urlPathMatching(movieDetailsUrl))
            .willReturn(WireMock.aResponse()
                    .withStatus(200)
                    .withBody(jsonBody)))
}