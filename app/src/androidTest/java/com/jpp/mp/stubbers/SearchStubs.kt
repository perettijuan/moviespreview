package com.jpp.mp.stubbers

import com.github.tomakehurst.wiremock.client.WireMock
import com.jpp.mpdata.api.BaseUrlChangingInterceptorProvider

private const val searchUrl = "/3/search/multi"
/**
 * Stubs the movie list now playing section to show the first page.
 */
fun stubSearchDefault() {
    BaseUrlChangingInterceptorProvider.setInterceptor(LOCAL_HOST + searchUrl)
    val jsonBody = readAssetFrom("search_result_page.json")
    WireMock.stubFor(WireMock.get(WireMock.urlPathMatching(searchUrl))
            .willReturn(WireMock.aResponse()
                    .withStatus(200)
                    .withBody(jsonBody)))
}

/**
 * Stubs the search section to fail with the [errorCode] provided.
 */
fun stubSearchWithError(errorCode: Int = 400) {
    BaseUrlChangingInterceptorProvider.setInterceptor(LOCAL_HOST + searchUrl)
    WireMock.stubFor(WireMock.get(WireMock.urlPathMatching(searchUrl))
            .willReturn(WireMock.aResponse()
                    .withStatus(errorCode)
            ))
}
