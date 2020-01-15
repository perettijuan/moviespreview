package com.jpp.mp.stubbers

import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching
import com.jpp.mpdata.api.BaseUrlChangingInterceptorProvider

/**
 * Stubs the movie list now playing section to show the first page.
 */
fun stubConfigurationDefault() {
    val url = "/3/configuration"
    BaseUrlChangingInterceptorProvider.setInterceptor(LOCAL_HOST + url)
    val jsonBody = readAssetFrom("configuration_default.json")
    stubFor(get(urlPathMatching(url))
            .willReturn(aResponse()
                    .withStatus(200)
                    .withBody(jsonBody)))
}
