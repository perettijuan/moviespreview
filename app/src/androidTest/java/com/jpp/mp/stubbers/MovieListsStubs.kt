package com.jpp.mp.stubbers

import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.jpp.mpdata.api.BaseUrlChangingInterceptorProvider
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching

/**
 * Stubs used in the movie list section.
 */
object MovieListsStubs {

    fun stubMovieListError(errorCode: Int) {
        val url = "/movie/now_playing"
        BaseUrlChangingInterceptorProvider.setInterceptor(LOCAL_HOST + url)
        stubFor(get(urlPathMatching(url))
                .willReturn(aResponse()
                        .withStatus(errorCode)
                ))
    }


    val LOCAL_HOST = "http://127.0.0.1:8080"
}