package com.jpp.mp.stubbers

import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching
import com.jpp.mpdata.api.BaseUrlChangingInterceptorProvider

private const val movieDetailsUrl = "/3/movie/419704.0"
private const val movieStateUrl = "/3/movie/419704.0/account_states"

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
fun stubMovieDetailsWithError(errorCode: Int = 400) {
    BaseUrlChangingInterceptorProvider.setInterceptor(LOCAL_HOST + movieDetailsUrl)
    stubFor(get(urlPathMatching(movieDetailsUrl))
            .willReturn(aResponse()
                    .withStatus(errorCode)
            ))
}

fun stubMovieStateFlavor1() = stubMovieState("movie_state_flavor_1.json")
fun stubMovieStateFlavor2() = stubMovieState("movie_state_flavor_2.json")
fun stubMovieStateFlavor3() = stubMovieState("movie_state_flavor_3.json")
fun stubMovieStateFlavor4() = stubMovieState("movie_state_flavor_4.json")

private fun stubMovieState(filePath: String) {
    BaseUrlChangingInterceptorProvider.setInterceptor(LOCAL_HOST + movieStateUrl)
    val jsonBody = readAssetFrom(filePath)
    stubFor(get(urlPathMatching(movieStateUrl))
            .willReturn(aResponse()
                    .withStatus(200)
                    .withBody(jsonBody)))
}
