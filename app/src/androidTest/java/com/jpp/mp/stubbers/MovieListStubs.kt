package com.jpp.mp.stubbers


import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching
import com.jpp.mpdata.api.BaseUrlChangingInterceptorProvider


/**
 * This file contains all the stub functions used in the movie lists sections.
 */

const val LOCAL_HOST = "http://127.0.0.1:8080"

/**
 * Stubs the movie list now playing section to fail with the [errorCode] provided.
 */
fun stubNowPLayingWithError(errorCode: Int = 400) {
    val url = "/3/movie/now_playing"
    BaseUrlChangingInterceptorProvider.setInterceptor(LOCAL_HOST + url)
    stubFor(get(urlPathMatching(url))
            .willReturn(aResponse()
                    .withStatus(errorCode)
            ))
}

/**
 * Stubs the movie list now playing section to show the first page.
 */
fun stubNowPlayingFirstPage() {
    val url = "/3/movie/now_playing"
    BaseUrlChangingInterceptorProvider.setInterceptor(LOCAL_HOST + url)
    val jsonBody = readAssetFrom("movies_now_playing_page_1.json")
    stubFor(get(urlPathMatching(url))
            .willReturn(aResponse()
                    .withStatus(200)
                    .withBody(jsonBody)))
}