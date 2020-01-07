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
private const val playingListUrl = "/3/movie/now_playing"

/**
 * Stubs the movie list now playing section to fail with the [errorCode] provided.
 */
fun stubNowPlayingWithError(errorCode: Int = 400) {
    BaseUrlChangingInterceptorProvider.setInterceptor(LOCAL_HOST + playingListUrl)
    stubFor(get(urlPathMatching(playingListUrl))
            .willReturn(aResponse()
                    .withStatus(errorCode)
            ))
}

/**
 * Stubs the movie list now playing section to show the first page.
 */
fun stubNowPlayingFirstPage() {
    BaseUrlChangingInterceptorProvider.setInterceptor(LOCAL_HOST + playingListUrl)
    val jsonBody = readAssetFrom("movies_now_playing_page_1.json")
    stubFor(get(urlPathMatching(playingListUrl))
            .willReturn(aResponse()
                    .withStatus(200)
                    .withBody(jsonBody)))
}