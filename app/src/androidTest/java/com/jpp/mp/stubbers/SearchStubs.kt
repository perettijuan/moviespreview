package com.jpp.mp.stubbers

import com.github.tomakehurst.wiremock.client.WireMock
import com.jpp.mpdata.api.BaseUrlChangingInterceptorProvider

/**
 * Stubs the movie list now playing section to show the first page.
 */
fun stubSearchDefault() {
    val url = "/3/search/multi"
    BaseUrlChangingInterceptorProvider.setInterceptor(LOCAL_HOST + url)
    val jsonBody = readAssetFrom("search_result_page.json")
    WireMock.stubFor(WireMock.get(WireMock.urlPathMatching(url))
            .willReturn(WireMock.aResponse()
                    .withStatus(200)
                    .withBody(jsonBody)))
}